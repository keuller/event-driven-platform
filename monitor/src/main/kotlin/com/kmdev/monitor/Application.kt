package com.kmdev.monitor

import io.vertx.reactivex.core.RxHelper
import io.vertx.reactivex.core.Vertx
import org.slf4j.LoggerFactory

class Application

fun main(args: Array<String>) {

    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")

    val log = LoggerFactory.getLogger(Application::class.java.name)
    val vertx = Vertx.vertx()

    Runtime.getRuntime().addShutdownHook(Thread {
        log.info("Monitor module has been finished.")
        vertx.close()
    })

    RxHelper.deployVerticle(vertx, MonitorVerticle())
            .subscribe({ _ -> log.info("Monitor module has been started.") }, { _ -> System.exit(-1) })
}
