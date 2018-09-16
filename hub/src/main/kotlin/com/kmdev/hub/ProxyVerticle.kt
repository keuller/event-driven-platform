package com.kmdev.hub

import com.kmdev.common.PlatformVerticle
import com.kmdev.common.genMessageId
import com.kmdev.common.moduleName
import io.reactivex.subjects.BehaviorSubject
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.RxHelper
import io.vertx.reactivex.core.http.HttpServerResponse
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class ProxyVerticle : PlatformVerticle() {
    private val log = LoggerFactory.getLogger(ProxyVerticle::class.java.name)

    private val JSON_TYPE = "application/json"

    private val OUTBOUND = "/outbound"

    private val response = BehaviorSubject.create<String>()

    override fun start(future: Future<Void>) {
        connectorName = moduleName("proxy")
        connect(OUTBOUND) { msg -> response.onNext(msg) }

        val routes = createRouter()
        vertx.createHttpServer()
                .requestHandler { routes.accept(it) }
                .rxListen(8080, "localhost")
                .doOnError { err -> future.fail(err) }
                .doOnSuccess { _ -> future.complete() }
                .subscribe()
    }

    override fun stop(future: Future<Void>) {
        connector.disconnect()
        future.complete()
    }

    private fun createRouter() = Router.router(vertx).apply {
        route().handler(BodyHandler.create())
        route().failureHandler(handlerError)
        get("/").handler(handlerIndex)
        get("/health").handler(handlerHealth).produces(JSON_TYPE)
        post("/event").handler(handlerEvent).produces(JSON_TYPE)
    }

    private val handlerError = Handler<RoutingContext> { req->
        req.response().statusCode = 500
        req.response().statusMessage = "Ops! Something goes wrong."
    }

    private val handlerIndex = Handler<RoutingContext> { req ->
        req.response().end("Hub service.")
    }

    private val handlerEvent = Handler<RoutingContext> { req ->
        val data = req.bodyAsJson

        if (isInvalid(data)) {
            log.warn("Invalid payload data ${data.encode()}")
            notAcceptable(req.response(), "Invalid event payload.")
            return@Handler
        }

        if (isInvalidType(data.getString("type"))) {
            log.warn("Invalid event type.")
            notAcceptable(req.response(), "Invalid event type.")
            return@Handler
        }

        val topicName = "/${data.getString("type")}"
        if (notCommand(data)) {
            connector.publish(topicName, data.encode())
            json(req.response(), mapOf("message" to "Ok"))
            return@Handler
        }

        handleCommand(topicName, data, req.response())
    }

    private fun handleCommand(topicName: String, data: JsonObject, resp: HttpServerResponse) {
        val messageId = genMessageId()
        connector.publish(topicName, data.put("id", messageId).encode())

        val reply = response.timeout(2L, TimeUnit.SECONDS)
                .map { msg -> JsonObject(msg) }
                .filter { json -> messageId.equals(json.getString("id"), true) }
                .subscribeOn(RxHelper.scheduler(vertx))
                .take(1).singleOrError()

        reply.doOnError { err -> fail(resp, err.message) }
                .doOnSuccess { json -> json.remove("id"); resp.endJson(json) }
                .subscribe()
    }

    private val handlerHealth = Handler<RoutingContext> { req ->
        val dt = Date().toInstant().toString()
        req.response().endJson(mapOf("code" to "0", "status" to "OK", "date_time" to dt))
    }

    fun HttpServerResponse.endJson(obj: Any) {
        this.putHeader("Content-Type", JSON_TYPE).end(Json.encodePrettily(obj))
    }
}
