package io.s7i.token;

import io.s7i.vertx.AsyncOp;
import io.s7i.vertx.Configuration;
import io.vertx.core.Future;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class UserHandler {

    public static final String ROOT = Configuration.CONTEXT_ROOT.get();
    public static final String LOGOUT = ROOT + "user/logout";
    public static final String STATUS = ROOT + "user/status";
    public static final String TOKEN = ROOT + "user/token";

    public static Router init(Router router, AsyncOp asyncOp, UserTokenGenerator generator) {

        router.get(TOKEN)
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .respond(ctx ->
                        Optional.ofNullable(ctx.user())
                                .map(User::principal)
                                .map(p -> p.getString("userName"))
                                .map(usrName -> asyncOp.roles(usrName)
                                        .compose(roles -> Future.succeededFuture(generator.generate(usrName, roles)))
                                ).orElse(Future.succeededFuture(new JsonObject()))
                );
        router.get(STATUS)
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .respond(ctx -> {
                    var principal = Optional.ofNullable(ctx.user())
                            .map(User::principal);
                    return Future.succeededFuture(new JsonObject()
                            .put("authOk", principal.isPresent())
                            .put("principal", principal.orElse(new JsonObject())));

                });

        router.route(LOGOUT)
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .respond(ctx -> {
                    ctx.session().destroy();
                    return Future.succeededFuture(new JsonObject().put("ok", true));
                });

        return router;
    }
}
