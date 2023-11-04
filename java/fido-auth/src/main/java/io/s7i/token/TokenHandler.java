package io.s7i.token;

import io.s7i.vertx.AsyncOp;
import io.vertx.core.Future;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Slf4j
public class TokenHandler {

    public static Router attachRoute(Router router, AsyncOp asyncOp) {

        router.get("/token")
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .respond(ctx -> {
                    var userName = requireNonNull(ctx.user().principal().getString("userName"));
                    return asyncOp.roles(userName)
                            .compose(roles -> Future.succeededFuture(generate(roles)));
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
