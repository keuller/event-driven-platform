package com.kmdev.security;

import com.kmdev.common.mqtt.Client;
import com.kmdev.common.mqtt.Message;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import static java.util.Objects.nonNull;

public class SecurityVerticle extends AbstractVerticle {
    private final Logger log = LoggerFactory.getLogger(SecurityVerticle.class.getName());

    private static final String INBOUND = "/security";

    private static final String MONITOR = "/monitor";

    private static final String OUTBOUND = "/outbound";

    private static final String BROKER_HOST = System.getenv().getOrDefault("BROKER_HOST", "localhost");

    private static final int BROKER_PORT = Integer.parseInt(System.getenv().getOrDefault("BROKER_PORT", "1883"));

    private static final String MODULE_ID = Message.module("security");

    private final UserService service = new UserService();

    private Client client;

    private BiFunction<String, JsonObject, JsonObject> processCommand = (cmd, data) -> {
        switch(cmd) {
            case "auth": return service.authenticate.apply(data);
            case "create": {
                client.publish(MONITOR, new JsonObject().put("command", "create_user").put("username", data.getString("username")).put("timestamp", new Date().toString()).encode());
                return service.createUser.apply(data);
            }
            default: return new JsonObject().put("message", "Invalid command.");
        }
    };

    private Consumer<String> consumer = (payload) -> {
        final JsonObject data = new JsonObject(payload);
        final String msgId = data.getString("id");
        final String cmd = data.getString("command");
        final JsonObject response = processCommand.apply(cmd, data.getJsonObject("payload"));
        if (client.isConnected() && nonNull(msgId)) {
            log.debug(response.put("id", msgId).encode());
            client.publish(OUTBOUND, response.put("id", msgId).encode());
        }
    };

    @Override
    public void start(Future<Void> future) {
        try {
            buildMqttClient();
            future.complete();
        } catch (Exception ex) {
            future.fail(ex);
        }
    }

    public void stop(Future<Void> future) {
        client.disconnect();
        future.complete();
    }

    private void buildMqttClient() throws Exception {
        client = new Client(MODULE_ID, BROKER_HOST, BROKER_PORT);
        client.connect();
        client.setProcessor(consumer);
        client.subscribe(INBOUND);
    }
}
