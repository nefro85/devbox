package io.s7i.webauthn;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import io.s7i.vertx.Configuration;
import io.vertx.core.Future;
import io.vertx.ext.auth.webauthn.Authenticator;
import lombok.Data;
import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoRepository implements AuthenticatorRepository {

    static final Logger log = LoggerFactory.getLogger(MongoRepository.class);
    private final MongoClient client;

    @Data
    public static class Details {

        static Details from(Authenticator authenticator) {
            var details = new Details();
            details.counter = authenticator.getCounter();
            details.credID = authenticator.getCredID();
            details.flags = authenticator.getFlags();
            details.fmt = authenticator.getFmt();
            details.publicKey = authenticator.getPublicKey();
            details.type = authenticator.getType();
            details.userName = authenticator.getUserName();
            return details;
        }

        Long counter;
        String credID;
        Integer flags;
        String fmt;
        String publicKey;
        String type;
        String userName;

        public Authenticator toAuthenticator() {
            var a = new Authenticator();

            a.setCounter(counter);
            a.setCredID(credID);
            a.setFlags(flags);
            a.setFmt(fmt);
            a.setPublicKey(publicKey);
            a.setType(type);
            a.setUserName(userName);

            return a;
        }
    }

    @Data
    public static class Auth {
        ObjectId id;
        Details authenticator;

        String created;
        String update;
    }

    public MongoRepository() {
        String mongoUri = Configuration.MONGODB_URI.get();
        var connectionString = new ConnectionString(mongoUri);
        var pojoCodecRegistry = fromProviders(PojoCodecProvider.builder()
                .register(Auth.class)
                .register(Details.class)
                .build());
        var codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        var clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        client = MongoClients.create(clientSettings);
    }

    public MongoCollection<Auth> collection() {
        return client.getDatabase("webauthn").getCollection("fido", Auth.class);
    }

    @Override
    public Future<List<Authenticator>> fetcher(Authenticator query) {
        log.info("fetch: {}", query);

        var split = collection()
                .find(Filters.or(
                        Filters.eq("authenticator.userName", query.getUserName()),
                        Filters.eq("authenticator.credID", query.getCredID())))
                .spliterator();
        var list = StreamSupport.stream(split, false)
                .map(Auth::getAuthenticator)
                .map(Details::toAuthenticator)
                .collect(Collectors.toList());
        return Future.succeededFuture(list);
    }

    @Override
    public Future<Void> updater(Authenticator authenticator) {

        Auth auth = collection()
                .find(Filters.eq("authenticator.credID", authenticator.getCredID()))
                .first();

        if (auth != null) {
            log.info("updating counter");

            auth.getAuthenticator().setCounter(authenticator.getCounter());
            auth.setUpdate(Instant.now().toString());

            var flt = new Document("_id", auth.getId());
            var options = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);

            collection().findOneAndReplace(flt, auth, options);

        } else {

            auth = new Auth();
            auth.setAuthenticator(Details.from(authenticator));
            String created = Instant.now().toString();

            auth.setCreated(created);

            var result = collection().insertOne(auth);

            log.info("authenticator added: {}, result: {}", authenticator, result);
        }

        return Future.succeededFuture();
    }
}
