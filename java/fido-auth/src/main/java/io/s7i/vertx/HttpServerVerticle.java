package io.s7i.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class HttpServerVerticle extends AbstractVerticle {
    public static final String HOST = "0.0.0.0";
    public static final String PORT = "8443";
    public static final String WEB_ROOT = Configuration.WEB_ROOT.get();

    protected abstract Router initRouter();

    @Override
    public void start(Promise<Void> start) throws Exception {

        var options = new HttpServerOptions()
              .setLogActivity(Boolean.parseBoolean(Configuration.SHOW_ACTIVITY.get()))
              .setSsl(Boolean.parseBoolean(Configuration.USE_SSL.get()))
              .setKeyStoreOptions(
                    new JksOptions()
                          .setPath(Configuration.CERT_PATH.get())
                          .setPassword(Configuration.CERTSTORE_SECRET.get()));

        vertx.createHttpServer(options)
              .requestHandler(initRouter())
              .listen(getServerPort(), getServerHost())
              .onSuccess(v -> {
                  log.info("Server Running");
                  start.complete();
              })
              .onFailure(start::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        log.info("Stop called.");
        stopPromise.complete();
    }

    private int getServerPort() {
        return Configuration.SERVER_PORT.find()
              .map(Integer::parseInt)
              .orElseThrow();
    }

    private String getServerHost() {
        return Configuration.SERVER_HOST.get();
    }
}
