package com.kmdev.hub;

import io.reactivex.Single;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    public static void main(String... args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

        final Vertx vertx = Vertx.vertx();
        final Logger log = LoggerFactory.getLogger(Application.class.getName());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Hub server has been finished.");
            vertx.close();
        }));

        Single<String> eventRouter = RxHelper.deployVerticle(vertx, new DispatcherVerticle());
        Single<String> proxyServer = RxHelper.deployVerticle(vertx, new ProxyVerticle());
        Single.concat(eventRouter, proxyServer).count()
            .subscribe(val -> log.info("Hub Application has been started."), err -> System.exit(-1));
    }

}
