package io.s7i.vertx;

import io.s7i.token.UserHandler;
import io.s7i.token.UserTokenGenerator;
import io.s7i.webauthn.MongoRepository;
import io.s7i.webauthn.Repository;
import io.s7i.webauthn.RocksdbRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class AuthServer extends AbstractVerticle {
    public static final String HOST = "0.0.0.0";
    public static final String PORT = "8443";
    public static final String WEB_ROOT = Configuration.WEB_ROOT.get();
    protected AsyncOp repo;
    protected final UserTokenGenerator userTokenGenerator = new UserTokenGenerator();

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        var repoFactory = Map.<String, Supplier<Repository>>of(
                "mongodb", MongoRepository::new,
                "rocksdb", RocksdbRepository::new
        ).get(Configuration.REPO_TYPE.get());

        repo = new AsyncOp(vertx, repoFactory.get());
    }

    Router initRouter() {
        final Router router = Router.router(vertx);
        router.route(staticRoutePath()).handler(StaticHandler.create(WEB_ROOT).setCachingEnabled(false));
        router.post().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        UserHandler.init(router, repo, userTokenGenerator);
        AuthnHelper.initAuthun(vertx, repo, router);

        return router;
    }

    private String staticRoutePath() {
        return Configuration.CONTEXT_ROOT.get() + "*";
    }

    @Override
    public void start(Promise<Void> start) throws Exception {

        var options = new HttpServerOptions()
                .setLogActivity(Boolean.parseBoolean(Configuration.SHOW_ACTIVITY.get()))
                .setSsl(Boolean.parseBoolean(Configuration.USE_SSL.get()))
                .setKeyStoreOptions(
                        new JksOptions()
                                .setPath(Configuration.CERT_PATH.get())
                                .setPassword(Configuration.CERTSTORE_SECRET.get()));

        vertx.createHttpServer(options)
                .requestHandler(initRouter())
                .listen(getServerPort(), getServerHost())
                .onSuccess(v -> {
                    log.info("Server Running");
                    start.complete();
                })
                .onFailure(start::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        log.info("Stop called.");
    }

    private int getServerPort() {
        return Configuration.SERVER_PORT.find()
                .map(Integer::parseInt)
                .orElseThrow();
    }

    private String getServerHost() {
        return Configuration.SERVER_HOST.get();
    }
}
