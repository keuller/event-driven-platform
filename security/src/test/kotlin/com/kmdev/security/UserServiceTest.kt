package com.kmdev.security

import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner::class)
class UserServiceTest {

    @Before fun setUp() {
        val user = JsonObject()
                .put("username", "test")
                .put("password", "123456")
                .put("email", "test@test.com")
                .put("role", "user")
        createUser(user)
    }

    @Test fun testCreateUser(ctx: TestContext) {
        val user = JsonObject()
                .put("username", "test")
                .put("password", "test")
                .put("email", "test@test.com")
                .put("role", "admin")

        val result = handleCommand("create", user)
        ctx.assertEquals(result.getString("message"), "User has been created.")
    }

    @Test fun testCreateInvalidUser(ctx: TestContext) {
        val user = JsonObject()
                .put("username", "")
                .put("password", "test")
                .put("email", "")
                .put("role", "")

        val result = createUser(user)
        ctx.assertEquals(result.getString("message"), "Invalid user data.")
    }

    @Test fun testAuthenticate(ctx: TestContext) {
        val result = handleCommand("auth", JsonObject().put("username", "test").put("password", "test"))
        ctx.assertEquals(result.getString("message"), "User not found.")
    }

    @Test fun testAuthenticateInvalidCredentials(ctx: TestContext) {
        val result = authenticate("test", "")
        println(result.getString("message"))
        ctx.assertEquals(result.getString("message"), "Invalid credentials.")
    }

    @Test fun testFindAll(ctx: TestContext) {
        val users = findAll()
        val userArray = users.getJsonArray("users")
        ctx.assertEquals(userArray.size(), 2)
    }

    @Test fun testHandleCommand(ctx: TestContext) {
        val json = handleCommand("xpto", JsonObject())
        ctx.assertEquals(json.getString("message"), "Invalid command.")
    }

}
