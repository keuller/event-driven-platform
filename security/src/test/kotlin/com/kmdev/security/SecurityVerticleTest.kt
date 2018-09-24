package com.kmdev.security

import io.reactivex.Single
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.reactivex.core.RxHelper
import io.vertx.reactivex.core.Vertx
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(VertxUnitRunner::class)
class SecurityVerticleTest {

    val vertx = Vertx.vertx()

    var verticleId: String = ""

    @Test
    fun start(ctx: TestContext) {
        val async = ctx.async()
        RxHelper.deployVerticle(vertx, SecurityVerticle())
                .doOnError(Throwable::printStackTrace)
                .doOnSuccess { vid ->
                    verticleId = vid
                }
                .subscribe()

        Single.just("").delay(2, TimeUnit.SECONDS)
                .doOnSuccess {
                    vertx.rxUndeploy(verticleId).subscribe()
                    async.complete()
                }
                .subscribe()
    }

    @Test fun testHandleCommand() {

    }
}