package io.s7i.vertx;

import io.s7i.token.TokenHandler;
import io.s7i.webauthn.MongoRepository;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class AuthServer extends AbstractVerticle {
    AsyncOp repo;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        repo = new AsyncOp(vertx, new MongoRepository());
    }

    Router initRouter() {
        final Router router = Router.router(vertx);
        router.route().handler(StaticHandler.create(Configuration.WEB_ROOT.get()).setCachingEnabled(false));
        router.post().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        initUserHandler(router);

        AuthnHelper.initAuthun(vertx, repo, router);
        TokenHandler.attachRoute(router, repo);

        return router;
    }

    private static void initUserHandler(Router router) {
        router.get("/userStatus")
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .respond(ctx -> {
                    var principal = Optional.ofNullable(ctx.user())
                            .map(User::principal);
                    return Future.succeededFuture(new JsonObject()
                            .put("authOk", principal.isPresent())
                            .put("principal", principal.orElse(new JsonObject())));

                });

        router.route("/logout").handler(ctx -> {
            ctx.session().destroy();
            ctx.response().end("LOGGED OUT!");
        });
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
        vertx.createHttpServer(
                        options)
                .requestHandler(initRouter())
                .listen(8443, "0.0.0.0")
                .onSuccess(v -> {
                    log.info("Server Running");
                    start.complete();
                })
                .onFailure(start::fail);
    }
}
