package com.kmdev.common

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.util.*

fun genMessageId(): String {
    val token = UUID.randomUUID().toString().split("-").last()
    val serial = Date().time
    return "$token-$serial"
}

fun moduleName(name: String): String {
    val token = UUID.randomUUID().toString().split("-").last()
    return "$name-$token"
}

class Connector(name: String, host: String, port: Int) : MqttCallback {
    private val log = LoggerFactory.getLogger("com.kmdev.common.Connector")

    private val MAX_RETRY_SERVER = 99

    private var client: MqttClient

    private val QOS: Int = 2

    private var connected: Boolean = false

    private var topicName: String = ""

    private val persistence = MemoryPersistence()

    private val options = MqttConnectOptions()

    private lateinit var handler: (msg: String) -> Unit

    init {
        options.connectionTimeout = 5000
        options.keepAliveInterval = 10_000
        options.isCleanSession = true
        options.isAutomaticReconnect = false
        try {
            this.client = MqttClient("tcp://$host:$port", name, persistence)
        } catch (ex: MqttException) {
            throw Exception(ex)
        }
    }

    fun eventHandler(evtHandler: (msg: String) -> Unit): Connector {
        this.handler = evtHandler
        return this
    }

    fun isConnected(): Boolean = this.connected

    fun connect(): Connector {
        try {
            this.client.connect(options)
            this.connected = true
        } catch (ex: MqttException) {
            log.error(ex.message)
        }
        return this
    }

    fun disconnect() {
        try {
            this.client.disconnect()
            this.connected = false
        } catch (ex: MqttException) {
            log.error(ex.message)
        }

    }

    fun subscribe(topic: String) {
        this.topicName = topic
        try {
            this.client.setCallback(this)
            this.client.subscribe(topic)
        } catch(ex: MqttException) {
            log.error(ex.message)
        }
    }

    fun publish(topic: String, message: String) {
        try {
            val msg = MqttMessage(message.toByteArray())
            msg.qos = QOS
            this.client.publish(topic, msg)
        } catch(ex: MqttException) {
            log.error(ex.message)
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }

    override fun messageArrived(id: String?, message: MqttMessage?) {
        this.handler.invoke(String(message!!.payload, Charset.defaultCharset()))
    }

    override fun connectionLost(error: Throwable?) {
        log.warn("The connection $this.name lost connection from server.")
        this.connected = false
        var attempts = 0
        do {
            attempts++
            try { Thread.sleep(5000); } catch (ex: Exception ) {}
            log.warn("trying to reconnect to the server...")
            this.connect()
        } while (attempts < MAX_RETRY_SERVER && !connected)

        if (connected) {
            log.info("Reconnected to the server successfully.")
            this.subscribe(topicName)
        }
    }

}