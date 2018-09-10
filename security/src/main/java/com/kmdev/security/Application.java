package com.kmdev.security;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application bootstrap class.
 */
public class Application {

    public static void main(String... args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

        final Vertx vertx = Vertx.vertx();
        final Logger log = LoggerFactory.getLogger(Application.class.getName());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Security module has been finished.");
            vertx.close();
        }));

        RxHelper.deployVerticle(vertx, new SecurityVerticle())
            .subscribe(val -> log.info("Security module has been started."), err -> System.exit(-1));
    }

}
