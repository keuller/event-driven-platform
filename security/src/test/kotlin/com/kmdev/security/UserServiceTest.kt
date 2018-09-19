package com.kmdev.security

import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserServiceTest {

    @Test fun testAuthenticate() {
        val result = authenticate("test", "test")
        assertEquals(result.getString("message"), "User not found.")
    }

    @Test fun testFindAll() {
        val users = findAll()
        val userArray = users.getJsonArray("users")
        assertEquals(userArray.isEmpty, true)
    }

    @Test fun testCreateUser() {
        val user = JsonObject()
                .put("username", "test")
                .put("password", "test")
                .put("email", "test@test.com")
                .put("role", "admin")

        val result = createUser(user)
        assertEquals(result.getString("message"), "User has been created.")
    }

    @Test fun testCreateInvalidUser() {
        val user = JsonObject()
                .put("username", "test")
                .put("password", "test")

        val result = createUser(user)
        assertEquals(result.getString("message"), "Invalid user data.")
    }

}
