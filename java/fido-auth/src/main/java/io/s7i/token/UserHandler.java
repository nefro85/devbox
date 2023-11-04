package io.s7i.token;

import io.s7i.vertx.AsyncOp;
import io.vertx.core.Future;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class UserHandler {

    public static Router init(Router router, AsyncOp asyncOp) {

        router.get("/user/token")
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .respond(ctx ->
                        Optional.ofNullable(ctx.user())
                                .map(User::principal)
                                .map(p -> p.getString("userName"))
                                .map(usrName -> asyncOp.roles(usrName)
                                        .compose(roles -> Future.succeededFuture(generate(roles)))
                                ).orElse(Future.succeededFuture(new JsonObject()))
                );
        router.get("/user/status")
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .respond(ctx -> {
                    var principal = Optional.ofNullable(ctx.user())
                            .map(User::principal);
                    return Future.succeededFuture(new JsonObject()
                            .put("authOk", principal.isPresent())
                            .put("principal", principal.orElse(new JsonObject())));

                });

        router.route("/user/logout").handler(ctx -> {
            ctx.session().destroy();
            ctx.response().end("LOGGED OUT!");
        });
        return router;
    }

    private static JsonObject generate(List<String> roles) {

        var jsonObject = new JsonObject();
        var claims = Map.of("roles", String.join(",", roles));
        jsonObject.put("token", JwtToken.build(claims));
        return jsonObject;
    }

}
