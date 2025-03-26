package io.s7i.webauthn;

import io.s7i.vertx.AuthServer;
import io.vertx.core.Vertx;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try {
            var vertex = Vertx.vertx();
            var latch = new CountDownLatch(1);
            Runnable cleanup = () -> {
                log.info("running shutdown");
                vertex.close().onSuccess(e -> log.info("close success: {}", e));
                latch.countDown();
            };
            Runtime.getRuntime().addShutdownHook(new Thread(cleanup, "Shutdown"));

            vertex.deployVerticle(new AuthServer(), ar -> {
                if (ar.succeeded()) {
                    log.info("deployed");
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
}
