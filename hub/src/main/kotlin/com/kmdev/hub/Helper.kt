package com.kmdev.hub

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.http.HttpServerResponse

val EVENT_TYPES = listOf("monitor", "security", "processor", "exporter")

fun isInvalid(json: JsonObject) = json?.getString("type").isEmpty() || !json.containsKey("payload")

fun notCommand(json: JsonObject) = json.getString("command").isEmpty()

fun isInvalidType(type: String) = !EVENT_TYPES.contains(type)

fun notAcceptable(response: HttpServerResponse, msg: String) {
    response.apply {
        statusCode = 406
        end(Json.encode(JsonObject().put("message", msg)))
    }
}

fun fail(response: HttpServerResponse, str: String?) {
    response.apply {
        statusCode = 500
        statusMessage = str
    }
}

fun json(response: HttpServerResponse, obj: Any) {
    response.apply {
        statusCode = 200
        end(Json.encode(obj))
    }
}