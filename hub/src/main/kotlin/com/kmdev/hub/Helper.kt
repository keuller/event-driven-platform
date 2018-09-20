package com.kmdev.hub

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.http.HttpServerResponse
import java.util.Objects.isNull

val EVENT_TYPES = listOf("monitor", "security", "processor", "exporter")

fun isInvalidPayload(json: JsonObject): Boolean {
    if (isNull(json)) return false
    return json.getString("type").isEmpty() || !json.containsKey("payload")
}

fun notCommand(json: JsonObject): Boolean {
    if (isNull(json)) return false
    return json.getString("command").isEmpty()
}

fun isInvalidType(json: JsonObject) = !EVENT_TYPES.contains(json.getString("type"))

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