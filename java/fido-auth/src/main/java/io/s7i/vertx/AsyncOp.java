package io.s7i.vertx;

import io.s7i.webauthn.AuthenticatorRepository;
import io.s7i.webauthn.Repository;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.ext.auth.webauthn.Authenticator;

import java.util.List;

public class AsyncOp implements AuthenticatorRepository {

    final Vertx vertx;
    final Repository repo;
    private final WorkerExecutor worker;

    public AsyncOp(Vertx vertx, Repository repo) {
        this.vertx = vertx;
        this.repo = repo;

        worker = vertx.createSharedWorkerExecutor("repo-ops-pool");
    }

    @Override
    public Future<List<Authenticator>> fetcher(Authenticator query) {
        return worker.executeBlocking(() -> repo.fetcher(query), false);
    }

    @Override
    public Future<Void> updater(Authenticator authenticator) {
        return worker.executeBlocking(() -> repo.updater(authenticator), false);
    }

    public Future<List<String>> roles(String userName) {
        return worker.executeBlocking(() -> repo.getUserRoles(userName), false);
    }
}
