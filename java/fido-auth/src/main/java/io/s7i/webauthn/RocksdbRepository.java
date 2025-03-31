package io.s7i.webauthn;

import io.github.s7i.doer.domain.rocksdb.RocksDb;
import io.s7i.vertx.Configuration;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.webauthn.Authenticator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocksdbRepository implements Repository {

    public static final String CF_AUTHUN = "authun";
    public static final String USER_ROLE = "user-role";
    private final RocksDb rocksDb;

    public RocksdbRepository() {
        this(Configuration.ROCKSDB_PATH.get());
    }

    public RocksdbRepository(String dbPath) {
        rocksDb = new RocksDb(dbPath);
        rocksDb.initColumnFamilies(CF_AUTHUN, USER_ROLE);
    }

    @Override
    public List<Authenticator> fetcher(Authenticator query) {
        log.debug("Fetching, userName: {}, credId: {}", query.getUserName(), query.getCredID());

        if (query.getCredID() != null) {
            throw new UnsupportedOperationException("not yet implemented: query for Cred ID");
        }

        var userName = query.getUserName();
        return rocksDb.getAsString(CF_AUTHUN, userName)
              .map(value -> {
                  log.debug("getting from db: {}", value);
                  return fromJsonArray(new JsonArray(value)).toList();
              })
              .orElse(Collections.emptyList());
    }

    @Override
    public Void updater(Authenticator authenticator) {
        var userName = authenticator.getUserName();

        if (userName == null || userName.isBlank()) {
            throw new RuntimeException();
        }

        var update = new AtomicInteger();

        var list = rocksDb.getAsString(CF_AUTHUN, userName)
              .map(value -> {
                  log.debug("User {}, Authenticators: {}", userName, authenticator);
                  return fromJsonArray(new JsonArray(value));
              })
              .stream()
              .flatMap(Function.identity())
              .map(stored -> {
                  if (stored.getCredID().equals(authenticator.getCredID())) {
                      stored.setCounter(authenticator.getCounter());

                      log.debug("Updating Authenticator: {}", stored);

                      update.incrementAndGet();
                  }
                  return stored;
              }).toList();

        if (list.isEmpty()) {
            log.debug("No Authenticator updates, adding new: {}", authenticator);

            list = List.of(authenticator);
            update.incrementAndGet();

        } else if (update.get() == 0) {
            log.warn("Not existing authenticator for user {}", authenticator);
            throw new CannotAddAlreadyExistsException("Authenticator for user " + userName);
        }

        if (update.get() > 0) {

            var arr = new JsonArray();
            for (var e : list) {
                arr.add(e.toJson());
            }
            var result = arr.toBuffer().toString();
            log.debug("putting: key: {}, value: {}", userName, result);
            rocksDb.put(CF_AUTHUN, userName, result);
        }

        return null;
    }

    public List<String> getUserRoles(String user) {
        return rocksDb.getAsString(USER_ROLE, user).map(stored -> {
            var arr = new JsonArray(stored);
            List<String> list = new ArrayList<>(arr.size());
            for (int i = 0; i < arr.size(); i++) {
                list.add(arr.getString(i));
            }
            return list;
        }).orElse(Collections.emptyList());
    }

    private static Stream<Authenticator> fromJsonArray(JsonArray array) {
        return StreamSupport.stream(array.spliterator(), false)
              .map(JsonObject.class::cast)
              .map(Authenticator::new);
    }
}
