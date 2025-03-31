package io.s7i.vertx;

import io.s7i.token.UserHandler;
import io.s7i.webauthn.MongoRepository;
import io.s7i.webauthn.RocksdbRepository;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AuthServer extends HttpServerVerticle {

    protected AsyncOp repo;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        repo = new AsyncOp(vertx, switch (Configuration.REPO_TYPE.get()) {
            case "mongodb" -> new MongoRepository();
            case "rocksdb" -> new RocksdbRepository();
            default -> throw new IllegalStateException("invalid repo kind");
        });
    }

    protected Router initRouter() {
        final Router router = Router.router(vertx);
        router.route(staticRoutePath()).handler(StaticHandler.create(WEB_ROOT)
              .setCachingEnabled(staticCacheEnabled()));
        router.post().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        UserHandler.init(router, repo);
        AuthnHelper.initAuthun(vertx, repo, router);

        return router;
    }

    private boolean staticCacheEnabled() {
        return !Configuration.APP_FLAGS.list().contains("no-cache");
    }

    private String staticRoutePath() {
        return Configuration.CONTEXT_ROOT.get() + "*";
    }


}
