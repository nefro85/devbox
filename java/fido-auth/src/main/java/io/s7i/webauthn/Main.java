package io.s7i.webauthn;

import io.s7i.vertx.AuthServer;
import io.s7i.vertx.Configuration;
import io.s7i.vertx.OutpostVerticle;
import io.vertx.core.Vertx;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.debug("args: {}", (Object) args);

        try {
            logConfiguration();

            var vertex = Vertx.vertx();
            var verticle = Arrays.stream(args)
                  .findFirst()
                  .map(kind -> switch (kind) {
                      case "outpost" -> new OutpostVerticle();
                      default -> new AuthServer();
                  }).orElseThrow();

            var latch = new CountDownLatch(1);
            Runnable cleanup = () -> {
                log.info("running shutdown");
                vertex.close()
                      .onSuccess(e -> log.info("Shutdown - Vertex.close() success."))
                      .onFailure(e -> log.error("Shutdown - Vertex.close() FAILURE.", e.getCause()));
                latch.countDown();
            };
            Runtime.getRuntime().addShutdownHook(new Thread(cleanup, "Shutdown"));

            vertex.deployVerticle(verticle, ar -> {
                if (ar.succeeded()) {
                    log.info("Deployed Verticle: {}", verticle);
                } else {
                    log.error("deploy verticle problem", ar.cause());
                    System.exit(4);
                }
            });
            latch.await();
            log.info("end");
        } catch (Exception e) {
            log.error("failure", e);
            System.exit(4);
        }
    }

    private static void logConfiguration() {
        log.info(
              "Configuration: \n{}",
              Arrays.stream(Configuration.values())
                    .map(conf -> {
                        var name = conf.name();
                        return name
                              + ": "
                              + (name.toLowerCase().contains("secret")
                              || name.toLowerCase().contains("password")
                              ? " ******* "
                              : conf.find().orElse(""));
                    })
                    .collect(Collectors.joining("\n"))
        );
    }
}
