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
import io.vertx.ext.auth.webauthn.Authenticator;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Slf4j
public class MongoRepository implements Repository {
    public static final String DATABASE_NAME = "webauthn";
    private final MongoClient client;

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
        return client.getDatabase(DATABASE_NAME).getCollection("fido", Auth.class);
    }

    public List<Authenticator> fetcher(Authenticator query) {
        log.info("fetch: {}", query);

        var split = collection()
                .find(Filters.or(
                        Filters.eq("authenticator.userName", query.getUserName()),
                        Filters.eq("authenticator.credID", query.getCredID())))
                .spliterator();
        var authenticators = StreamSupport.stream(split, false)
                .map(Auth::getAuthenticator)
                .map(Details::toAuthenticator)
                .collect(Collectors.toList());

        log.info("authenticators: {}", authenticators);
        return authenticators;
    }

    public Void updater(Authenticator authenticator) {

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
        return null;
    }

    public List<String> getUserRoles(String user) {
        log.info("asking for UserRoles of user: {}:", user);

        var coll = client.getDatabase(DATABASE_NAME).getCollection("UserRoles");
        var userRoles = coll.find(Filters.eq("userName", user)).first();
        if (userRoles != null) {
            var roleList = userRoles.getList("roles", String.class);
            return Collections.unmodifiableList(roleList);
        }
        return List.of();
    }
}
