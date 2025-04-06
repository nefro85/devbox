package s7i;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public abstract class HttpServerVerticle extends AbstractVerticle {
    public static final String HOST = "0.0.0.0";
    public static final String PORT = "8443";
    public static final String WEB_ROOT = "/";
    public static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    protected abstract Router initRouter();

    @Override
    public void start(Promise<Void> start) throws Exception {

        var options = new HttpServerOptions();

        vertx.createHttpServer(options)
              .requestHandler(initRouter())
              .listen(getServerPort(), getServerHost())
              .onSuccess(v -> {
                  LOGGER.info("Server Running");
                  start.complete();
              })
              .onFailure(start::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        LOGGER.info("Stop called.");
        stopPromise.complete();
    }

    private int getServerPort() {
        return Optional.ofNullable(System.getenv("SERVER_PORT"))
                .map(Integer::parseInt)
              .or(() -> Optional.of(Integer.parseInt(PORT))).orElseThrow();
    }

    private String getServerHost() {
        return HOST;
    }
}
