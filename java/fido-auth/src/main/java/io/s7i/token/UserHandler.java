package io.s7i.token;

import static io.s7i.token.UserTokenGenerator.generate;

import io.s7i.vertx.AsyncOp;
import io.s7i.vertx.Configuration;
import io.vertx.core.Future;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.time.Duration;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class UserHandler {

    public static final String ROOT = Configuration.CONTEXT_ROOT.get();
    public static final String LOGOUT = ROOT + "user/logout";
    public static final String STATUS = ROOT + "user/status";
    public static final String TOKEN = ROOT + "user/token";
    public static final String CHECK = ROOT + "user/check/*";
    public static final String ANY_OTHER = "/*";

    public static Router init(Router router, AsyncOp asyncOp) {

        router.get(TOKEN)
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .respond(ctx ->
                        Optional.ofNullable(ctx.user())
                                .map(User::principal)
                                .map(p -> p.getString("userName"))
                                .map(usrName -> asyncOp.roles(usrName)
                                      .compose(roles -> {
                                          var token = generate(usrName, roles);
                                          ctx.response().addCookie(Cookie
                                                .cookie("s7i-jwt", token.jwt())
                                                .setDomain(Configuration.JWT_COOKIE_DOMAIN.get())
                                                .setPath("/")
                                                .setMaxAge(Duration.ofHours(Integer.parseInt(Configuration.JWT_TTL_HOURS.get())).toSeconds()));
                                          return Future.succeededFuture(token.asJson());
                                      })
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

        final Function<RoutingContext, Future<String>> chkUser = ctx -> {
            var principal = Optional.ofNullable(ctx.user())
                  .map(User::principal);
            var statusCode = principal.map(p -> 200).orElse(401);
            ctx.response().setStatusCode(statusCode);

            log.debug("response code {} for {}", statusCode, ctx.request().absoluteURI());

            return Future.succeededFuture("Status Code: " + statusCode);

        };
        router.route(CHECK)
              .produces(MimeMapping.getMimeTypeForExtension("text"))
              .respond(chkUser);

        if (!Configuration.APP_FLAGS.list().contains("no-any-chk")) {
            log.debug("enabling ANY CHECK ROUTE");
            router.route(ANY_OTHER)
                  .order(10)
                  .produces(MimeMapping.getMimeTypeForExtension("text"))
                  .respond(chkUser);
        }

        return router;
    }
}
