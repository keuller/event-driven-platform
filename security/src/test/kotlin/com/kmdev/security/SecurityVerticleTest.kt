package com.kmdev.security

import io.vertx.ext.unit.TestContext
import io.vertx.reactivex.core.RxHelper
import io.vertx.reactivex.core.Vertx
import org.junit.jupiter.api.Test

class SecurityVerticleTest {

    @Test fun testStart(ctx: TestContext) {
        val vertx = Vertx.vertx()
        RxHelper.deployVerticle(vertx, SecurityVerticle())
    }

}