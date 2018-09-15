package com.kmdev.security

import com.kmdev.common.PlatformVerticle
import com.kmdev.common.moduleName
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class SecurityVerticle : PlatformVerticle() {
    private val log = LoggerFactory.getLogger(SecurityVerticle::class.java.name)

    private val INBOUND = "/security"
    private val OUTBOUND = "/outbound"

    override fun start(future: Future<Void>) {
        connectorName = moduleName("security")
        connect(INBOUND, this::processMessage)
        future.complete()
    }

    override fun stop(future: Future<Void>) {
        connector.disconnect()
        future.complete()
    }

    fun processMessage(msg: String) {
        val data = JsonObject(msg)
        val msgId = data.getString("id")
        val cmd = data.getString("command")
        val result = handleCommand(cmd, data.getJsonObject("payload"))
        if (connector.isConnected() && msgId.isNotEmpty()) {
            log.debug(result.encode())
            connector.publish(OUTBOUND, result.put("id", msgId).encode())
        }
    }

    fun handleCommand(cmd: String, data: JsonObject): JsonObject {
        return when(cmd) {
            "auth" -> authenticate(data.getString("username"), data.getString("password"))
            "create" -> createUser(data)
            "findAll" -> findAll()
            else -> JsonObject().put("message", "Invalid command.")
        }
    }

}
