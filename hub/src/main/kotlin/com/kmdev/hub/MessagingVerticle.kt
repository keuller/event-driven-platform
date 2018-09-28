package com.kmdev.hub

import io.moquette.server.Server
import io.vertx.core.Future
import io.vertx.reactivex.core.AbstractVerticle
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

/**
 * This verticle create a Hub service that represents a event broker
 * used by other services to communicate with each other.
 */
class MessagingVerticle : AbstractVerticle() {
    private val log = LoggerFactory.getLogger(MessagingVerticle::class.java.getName())

    private val broker = Server()

    override fun start(future: Future<Void>) {
        val config = Properties()
        config["host"] = "localhost"
        config["port"] = "1883"

        try {
            broker.startServer(config)
            future.complete()
        } catch (ex: IOException) {
            log.error(ex.message)
            future.fail(ex)
        }
    }

    override fun stop(future: Future<Void>) {
        broker.stopServer()
        future.complete()
    }

}