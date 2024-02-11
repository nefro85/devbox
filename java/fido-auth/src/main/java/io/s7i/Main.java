package io.s7i;

import io.s7i.vertx.AuthServer;
import io.s7i.vertx.Configuration;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class Main {

    public static void main(String[] args) {

        log.info(
                "Configuration: \n{}",
                Arrays.stream(Configuration.values())
                        .map(conf -> conf.name() + ": " + conf.find().orElse(""))
                        .collect(Collectors.joining("\n"))
        );

        var vertex = Vertx.vertx();
        vertex.deployVerticle(new AuthServer());

        Runnable cleanup = () -> {
            log.info("running shutdown");
            vertex.close().onSuccess(e -> log.info("close success: {}", e));
        };
        Runtime.getRuntime().addShutdownHook(new Thread(cleanup));
    }
}
