package com.kmdev.hub;

import com.kmdev.common.mqtt.Message;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.*;
import io.moquette.server.Server;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class DispatcherVerticle extends AbstractVerticle {
    private final Logger log = LoggerFactory.getLogger(DispatcherVerticle.class.getName());

    private final Server broker = new Server();

    public void start(Future<Void> future) {
        final Properties config = new Properties();
        config.put("host", "localhost");
        config.put("port", "1883");
        try {
            broker.startServer(config);
            broker.addInterceptHandler(new LoggerInterceptHandler());
            future.complete();
        } catch (IOException ex) {
            future.fail(ex);
        }
    }

    public void stop(Future<Void> future) {
        broker.stopServer();
        future.complete();
    }

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
}
