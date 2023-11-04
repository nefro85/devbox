package io.s7i.token;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenHandler {
    public static Router attachRoute(Router router) {
        var hnd = new TokenHandler();
        router.get("/token").handler(hnd::getToken);
        return router;
    }

    public void getToken(RoutingContext ctx) {
        var r = ctx.response();
        r.headers().set(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("json"));
        r.end(generate(ctx).toBuffer());
    }

    private JsonObject generate(RoutingContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("token", JwtToken.build());
        return jsonObject;
    }

}
