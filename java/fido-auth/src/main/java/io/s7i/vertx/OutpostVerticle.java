package io.s7i.vertx;


import static io.s7i.token.UserHandler.ANY_OTHER;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.s7i.token.JwtToken;
import io.s7i.token.UserHandler;
import io.s7i.webauthn.Utils;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.Router;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OutpostVerticle extends HttpServerVerticle {

    private final Set<String> requiredRoles = new HashSet<>(Configuration.REQUIRED_ROLES.list());


    @Override
    protected Router initRouter() {
        var router = Router.router(getVertx());

        router.route(ANY_OTHER)
              .handler(ctx -> {
                  Cookie cookie = ctx.request().getCookie(UserHandler.COOKIE_NAME);
                  if (cookie != null) {
                      String value = cookie.getValue();
                      var jwt = JwtToken.getInstance()
                            .decode(value);
                      if (jwt.isPresent()) {
                          var roles = Utils.asList(jwt.get().getClaim("roles").asString());

                          var valid = requiredRoles.isEmpty() || requiredRoles.containsAll(roles);
                          int code = valid ? HttpResponseStatus.OK.code() : HttpResponseStatus.UNAUTHORIZED.code();

                          if (!valid && log.isDebugEnabled()) {
                              log.debug("Code: {}. Required roles {}, but has: {}", code, requiredRoles, roles);
                          }

                          ctx.response()
                                .setStatusCode(code)
                                .end();
                          return;
                      }
                  }
                  ctx.response()
                        .setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
                        .end();
              });

        return router;
    }
}
