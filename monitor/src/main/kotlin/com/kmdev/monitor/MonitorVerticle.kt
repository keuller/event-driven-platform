package com.kmdev.monitor

import com.kmdev.common.PlatformVerticle
import com.kmdev.common.moduleName
import io.vertx.core.Future
import org.slf4j.LoggerFactory

class MonitorVerticle : PlatformVerticle() {

    private val log = LoggerFactory.getLogger(MonitorVerticle::class.java.name)

    private val INBOUND = "/monitor"

    override fun start(future: Future<Void>) {
        connectorName = moduleName("monitor")
        connect(INBOUND, this::handleEvent)
        future.complete()
    }

    override fun stop(future: Future<Void>) {
        connector.disconnect()
        future.complete()
    }

    private fun handleEvent(msg: String) {
        log.info("Monitor: $msg")
    }

}
