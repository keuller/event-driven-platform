package com.kmdev.common

import io.vertx.reactivex.core.AbstractVerticle

open class PlatformVerticle : AbstractVerticle() {

    val BROKER_HOST = (System.getenv() as Map<String, String>).getOrDefault("BROKER_HOST", "localhost")

    val BROKER_PORT = (System.getenv() as Map<String, String>).getOrDefault("BROKER_PORT", "1883")

    lateinit var connector: Connector

    lateinit var connectorName: String

    fun connect(topic: String, handler: (value: String) -> Unit) {
        connector = Connector(connectorName, BROKER_HOST, BROKER_PORT.toInt())
        connector.connect()
        if (!connector.isConnected()) return
        connector.eventHandler(handler).subscribe(topic)
    }

}