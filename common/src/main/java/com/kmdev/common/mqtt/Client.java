package com.kmdev.common.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import static java.util.Objects.isNull;

public final class Client implements MqttCallback {

    private final Logger log = LoggerFactory.getLogger(Client.class.getName());

    private final int MAX_RETRY_SERVER = 99;

    private final String name;

    private String topicName;

    private MqttClient client;

    private final int QOS = 2;

    private boolean connected = false;

    private Consumer<String> processor;

    private final MemoryPersistence persistence = new MemoryPersistence();

    private final MqttConnectOptions opts = new MqttConnectOptions();

    public Client(final String name, String host, int port) throws Exception {
        try {
            opts.setCleanSession(true);
            opts.setKeepAliveInterval(10000);
            opts.setAutomaticReconnect(false);

            this.name = name;
            client = new MqttClient(String.format("tcp://%s:%d", host, port), name, persistence);
        } catch (MqttException ex) {
            log.error(ex.getMessage());
            throw new Exception(ex);
        }
    }

    public boolean isConnected() { return connected; }

    public void setProcessor(Consumer<String> bean) {
        this.processor = bean;
    }

    public void connect() {
        try {
            this.client.connect(opts);
            this.connected = true;
        } catch (MqttException ex) {
            log.error(ex.getMessage());
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
            this.connected = false;
        } catch (MqttException ex) {
            log.error(ex.getMessage());
        }
    }

    public void publish(String topicName, String message) {
        try {
            MqttMessage msg = new MqttMessage(message.getBytes());
            msg.setQos(QOS);
            client.publish(topicName, msg);
        } catch (MqttException ex) {
            log.error(ex.getMessage());
        }
    }

    public void subscribe(String topic) {
        try {
            this.topicName = topic;
            client.setCallback(this);
            client.subscribe(topicName);
        } catch (MqttException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn(String.format("%s - connection lost from server.", name));
        throwable.printStackTrace();
        this.connected = false;
        int attempts = 0;
        do {
            attempts++;
            try { Thread.sleep(5000); } catch (Exception ex) {}
            log.warn("trying to reconnect to the server...");
            this.connect();
        } while (attempts < MAX_RETRY_SERVER && !isConnected());

        if (isConnected()) {
            log.info("Reconnected to the server successfully.");
            this.subscribe(topicName);
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage msg) throws Exception {
        if (!isNull(this.processor)) {
            this.processor.accept(new String(msg.getPayload(), Charset.defaultCharset()));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("Delivery complete...");
    }

}
