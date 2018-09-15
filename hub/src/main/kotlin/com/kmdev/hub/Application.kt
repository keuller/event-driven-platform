package com.kmdev.hub

import io.vertx.reactivex.core.RxHelper
import io.vertx.reactivex.core.Vertx
import org.slf4j.LoggerFactory

class Application

fun main(args: Array<String>) {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")

    val vertx = Vertx.vertx()
    val log = LoggerFactory.getLogger(Application::class.java.name)
    Runtime.getRuntime().addShutdownHook(Thread {
        log.info("Hub server has been finished.")
        vertx.close()
    })

    RxHelper.deployVerticle(vertx, ProxyVerticle())
            .concatWith { RxHelper.deployVerticle(vertx, MessagingVerticle()) }
            .count()
            .doOnError { err -> log.error(err.message) }
            .doOnSuccess { _ -> log.info("Hub module has been started.") }
            .subscribe()
}
