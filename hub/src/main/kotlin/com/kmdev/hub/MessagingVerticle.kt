package com.kmdev.hub

import io.moquette.server.Server
import io.vertx.core.Future
import io.vertx.reactivex.core.AbstractVerticle
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

class MessagingVerticle : AbstractVerticle() {
    private val log = LoggerFactory.getLogger(MessagingVerticle::class.java.getName())

    private val broker = Server()

    override fun start(future: Future<Void>) {
        val config = Properties()
        config["host"] = "localhost"
        config["port"] = "1883"

        try {
            broker.startServer(config)
//            broker.addInterceptHandler(LoggerInterceptHandler())
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

    /*
    class LoggerInterceptHandler implements InterceptHandler {

        @Override
        public String getID() {
            return Message.module("log");
        }

        @Override
        public Class<?>[] getInterceptedMessageTypes() {
            return new Class[0];
        }

        @Override
        public void onConnect(InterceptConnectMessage interceptConnectMessage) {
            log.info("Connection has been made.");
        }

        @Override
        public void onDisconnect(InterceptDisconnectMessage interceptDisconnectMessage) {
            log.info("Connection closed.");
        }

        @Override
        public void onConnectionLost(InterceptConnectionLostMessage interceptConnectionLostMessage) {

        }

        @Override
        public void onPublish(InterceptPublishMessage msg) {
            log.info("Message published on topic " + msg.getTopicName());
        }

        @Override
        public void onSubscribe(InterceptSubscribeMessage msg) {
            log.info("Client connected.");
        }

        @Override
        public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
            log.info("Client unsubscription has been made.");
        }

        @Override
        public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
            log.info("Message acknowledged " + msg.getPacketID());
        }
    }

     */
}