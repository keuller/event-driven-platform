package com.kmdev.common

import io.vertx.reactivex.core.AbstractVerticle
import java.util.function.Consumer

class PlatformVerticle : AbstractVerticle() {

    internal val BROKER_HOST = (System.getenv() as Map<String, String>).getOrDefault("BROKER_HOST", "localhost")

    internal val BROKER_PORT = (System.getenv() as Map<String, String>).getOrDefault("BROKER_PORT", "1883")

    lateinit var connector: Connector

    lateinit var connectorName: String

    fun connect(topic: String, handler: Consumer<String>) {
        connector = Connector(connectorName, BROKER_HOST, BROKER_PORT.toInt())
        connector.connect()
        if (!connector.isConnected()) return
        connector.eventHandler(handler).subscribe(topic)
    }

}