package com.kmdev.hub;

import com.kmdev.common.mqtt.Client;
import com.kmdev.common.mqtt.Message;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ProxyVerticle extends AbstractVerticle {
    private final Logger log = LoggerFactory.getLogger(ProxyVerticle.class.getName());

    private static final String JSON_TYPE = "application/json";

    private static final List<String> EVENT_TYPES = Arrays.asList("monitor", "security", "processor", "exporter");

    private static final String BROKER_HOST = System.getenv().getOrDefault("BROKER_HOST", "localhost");

    private static final int BROKER_PORT = Integer.parseInt(System.getenv().getOrDefault("BROKER_PORT", "1883"));

    private static final String CLIENT_ID = Message.module("proxy");

    private Client client;

    private final BehaviorSubject<String> response$ = BehaviorSubject.create();

    private final Observable<JsonObject> reply$ = response$.map(value -> new JsonObject(value));

    public void start(Future<Void> future) {
        HttpServerOptions options = new HttpServerOptions()
                .setHost("0.0.0.0")
                .setPort(8080);

        final Router router = Router.router(vertx);
        router.route().failureHandler(errorHandle);
        router.route().handler(BodyHandler.create());

        router.get("/health").handler(healthHandler).produces("text/plain");
        router.post("/event").consumes(JSON_TYPE).handler(postHandler).produces(JSON_TYPE);

        try {
            buildMqttClient();
        } catch (Exception ex) {
            future.fail(ex);
            return;
        }

        final HttpServer httpServer = vertx.createHttpServer(options);
        httpServer.requestHandler(router::accept).rxListen()
                .subscribe(val -> future.complete(), future::fail);
    }

    public void stop(Future<Void> future) {
        client.disconnect();
        future.complete();
    }

    private final Consumer<String> outboundConsumer = (str) -> response$.onNext(str);

    private void buildMqttClient() throws Exception {
        client = new Client(CLIENT_ID, BROKER_HOST, BROKER_PORT);
        client.connect();
        if (!client.isConnected()) return;
        client.setEventHandler(outboundConsumer).subscribe(Topics.OUTBOUND);
    }

    private final Handler<RoutingContext> healthHandler = (ctx) -> {
        final HttpServerResponse response = ctx.response();
        final JsonObject data = new JsonObject()
                .put("type", "monitor")
                .put("data", "health")
                .put("timestamp", new Date().toString());
        client.publish(Topics.MONITIOR, data.encode());
        response.setStatusCode(200).end("Health");
    };

    private final Handler<RoutingContext> postHandler = (ctx) -> {
        final HttpServerResponse response = ctx.response();
        final JsonObject data = ctx.getBodyAsJson();

        if (isPayloadInvalid.apply(data)) {
            log.error("Invalid payload: ".concat(data.encode()));
            response.setStatusCode(406).end(new JsonObject().put("status", "Invalid event payload.").encode());
            return;
        }

        if (isInvalidEventType.apply(data.getString("type"))) {
            response.setStatusCode(406).end(new JsonObject().put("status", "Invalid event type.").encode());
            return;
        }

        final String topicName = "/".concat(data.getString("type"));

        if (!isCommand.apply(data)) {
            client.publish(topicName, data.encode());
            response.setStatusCode(200).end(new JsonObject().put("status", "ok").encode());
            return;
        }

        final String messageId = Message.messageId();
        client.publish(topicName, data.put("id", messageId).encode());

        Single<JsonObject> response$ = reply$.subscribeOn(RxHelper.scheduler(vertx))
            .timeout(2L, TimeUnit.SECONDS)
            .filter(json -> json.getString("id").equalsIgnoreCase(messageId))
            .take(1)
            .singleOrError();

        response$.onErrorReturnItem(new JsonObject().put("message", "Operation fail."))
                .subscribe(result -> {
            result.remove("id"); // remove message ID - just technical stuff
            response.setStatusCode(200).end(result.encode());
        }, err -> {
            log.error(err.getMessage());
            response.setStatusCode(500).end(new JsonObject().put("status", err.getMessage()).encode());
        });
    };

    private final Handler<RoutingContext> errorHandle = (ctx) -> {
        final HttpServerResponse response = ctx.response();
        response.setStatusCode(500).end("Health");
    };

    private static final Function<JsonObject, Boolean> isPayloadInvalid = (json) -> (
        isEmpty(json.getString("type")) || json.getJsonObject("payload") == null
    );

    private static final Function<String, Boolean> isInvalidEventType = (type) -> {
        Objects.requireNonNull(type);
        return !EVENT_TYPES.contains(type);
    };

    private static final Function<JsonObject, Boolean> isCommand = (json) -> {
        Objects.requireNonNull(json);
        return !isEmpty(json.getString("command"));
    };

    private static boolean isEmpty(String value) {
        Objects.requireNonNull(value);
        return (null == value || "".equals(value));
    }

}
