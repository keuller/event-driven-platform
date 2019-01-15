package com.kmdev.hub

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import io.vertx.reactivex.core.http.HttpServerResponse

val EVENT_TYPES = listOf("monitor", "security", "processor", "exporter")

fun isInvalidPayload(json: JsonObject): Boolean {
    if (json.isEmpty) return false
    return !json.containsKey("type") && !json.containsKey("payload")
}

fun notCommand(json: JsonObject): Boolean {
    if (json.isEmpty) return false
    return json.getString("command", "").isEmpty()
}

fun isInvalidType(json: JsonObject) = !EVENT_TYPES.contains(json["type"])

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