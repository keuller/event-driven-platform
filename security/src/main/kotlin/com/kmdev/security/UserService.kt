package com.kmdev.security

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.Objects.requireNonNull

internal val users = arrayListOf<User>()

fun handleCommand(cmd: String, data: JsonObject): JsonObject {
    return when(cmd) {
        "auth" -> authenticate(data.getString("username"), data.getString("password"))
        "create" -> createUser(data)
        "findAll" -> findAll()
        else -> JsonObject().put("message", "Invalid command.")
    }
}

fun authenticate(user: String, pass: String): JsonObject {
    if (user.isEmpty() || pass.isEmpty()) return JsonObject().put("message", "Invalid credentials.")
    return try {
        users.asSequence().filter { it.username.equals(user, true) && it.password == pass }
                .first()
                .toJson()
    } catch (ex: NoSuchElementException) {
        JsonObject().put("message", "User not found.")
    }
}

fun createUser(user: JsonObject): JsonObject {
    requireNonNull(user)
    val bean = User().fromJson(user)
    if (bean.username.isEmpty() || bean.email.isEmpty() || bean.role.isEmpty())
        return JsonObject().put("message", "Invalid user data.")
    users.add(bean)
    return JsonObject().put("message", "User has been created.")
}

fun findAll(): JsonObject {
    val result = JsonArray()
    users.forEach { result.add(it.toJson()) }
    return JsonObject().put("users", result)
}
