package s7i;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {

        LOGGER.debug("args: {}", (Object) args);

        try {

            var vertex = Vertx.vertx();
            var verticle = new ApiVerticle();

            var latch = new CountDownLatch(1);
            Runnable cleanup = () -> {
                LOGGER.info("running shutdown");
                vertex.close()
                      .onSuccess(e -> LOGGER.info("Shutdown - Vertex.close() success."))
                      .onFailure(e -> LOGGER.error("Shutdown - Vertex.close() FAILURE.", e.getCause()));
                latch.countDown();
            };
            Runtime.getRuntime().addShutdownHook(new Thread(cleanup, "Shutdown"));

            vertex.deployVerticle(verticle, ar -> {
                if (ar.succeeded()) {
                    LOGGER.info("Deployed Verticle: {}", verticle);
                } else {
                    LOGGER.error("deploy verticle problem", ar.cause());
                    System.exit(4);
                }
            });
            latch.await();
            LOGGER.info("end");
        } catch (Exception e) {
            LOGGER.error("failure", e);
            System.exit(4);
        }

    }
}
