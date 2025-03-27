package io.s7i.webauthn;

import io.github.s7i.doer.domain.rocksdb.RocksDb;
import io.s7i.vertx.Configuration;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.webauthn.Authenticator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        var userName = query.getUserName();
        var list = new ArrayList<Authenticator>();
        rocksDb.getAsString(CF_AUTHUN, userName).ifPresent(value -> {
            log.debug("getting from db: {}", value);
            var arr = new JsonArray(value);

            for (int i = 0; i < arr.size(); i++) {
                list.add(new Authenticator(arr.getJsonObject(i)));
            }
        });
        return Collections.unmodifiableList(list);
    }

    @Override
    public Void updater(Authenticator authenticator) {
        var userName = authenticator.getUserName();

        var list = new ArrayList<Authenticator>();

        var value = rocksDb.getAsString(CF_AUTHUN, userName);
        if (value.isPresent()) {
            var json = value.get();
            log.debug("getting from db: {}", json);
            var arr = new JsonArray(json);
            for (int i = 0; i < arr.size(); i++) {
                list.add(new Authenticator(arr.getJsonObject(i)));
            }
            var update = list.stream()
                  .filter(a -> a.getCredID().equals(authenticator.getCredID()))
                  .map(e -> e.setCounter(e.getCounter() + 1))
                  .count();

            log.debug("updates of records: {}", update);
        } else {
            list.add(authenticator);
        }
        var arr = new JsonArray();
        for (var e : list) {
            arr.add(e.toJson());
        }
        var result = arr.toBuffer().toString();
        log.debug("putting: key: {}, value: {}", userName, result);
        rocksDb.put(CF_AUTHUN, userName, result);

        return null;
    }

    @Override
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
}
