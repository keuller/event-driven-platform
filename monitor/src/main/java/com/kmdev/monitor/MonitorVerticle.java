package com.kmdev.monitor;

import com.kmdev.common.mqtt.Client;
import com.kmdev.common.mqtt.Message;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitor module.
 */
public final class MonitorVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(MonitorVerticle.class.getName());

    private static final String BROKER_HOST = System.getenv().getOrDefault("BROKER_HOST", "localhost");

    private static final int BROKER_PORT = Integer.parseInt(System.getenv().getOrDefault("BROKER_PORT", "1883"));

    private static final String TOPIC = "/monitor";

    private static final String CLIENT_ID = Message.module("monitor");

    private Client client;

    @Override
    public void start(Future<Void> future) {
        try {
            buildMqttClient();
            future.complete();
        } catch (Exception ex) {
            future.fail(ex);
        }
    }

    @Override
    public void stop(Future<Void> future) {
        client.disconnect();
        future.complete();
    }

    private void buildMqttClient() throws Exception {
        client = new Client(CLIENT_ID, BROKER_HOST, BROKER_PORT);
        client.connect();
        client.setProcessor(str -> log.info("Message payload: ".concat(str)));
        client.subscribe(TOPIC);
    }

}
