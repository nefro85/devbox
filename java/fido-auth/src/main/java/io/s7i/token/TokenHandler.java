package io.s7i.token;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenHandler implements Handler<RoutingContext> {
    public static Router attachRoute(Router router) {
        router.route("/token").handler(new TokenHandler());
        return router;
    }

    @Override
    public void handle(RoutingContext ctx) {
        var r = ctx.response();
        r.headers().set(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("json"));
        r.end(getToken(ctx).toBuffer());
    }

    private JsonObject getToken(RoutingContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("token", JwtToken.build());
        return jsonObject;
    }

}
