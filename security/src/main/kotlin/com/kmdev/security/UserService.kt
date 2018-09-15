package com.kmdev.security

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.Objects.requireNonNull

val users = arrayListOf<User>()

fun authenticate(user: String, pass: String): JsonObject {
    if (user.isEmpty() || pass.isEmpty()) return JsonObject().put("message", "Invalid credentials.")
    try {
        return users.filter { it.username.equals(user, true) && it.password.equals(pass) }
                .first()
                .toJson()
    } catch (ex: NoSuchElementException) {
        return JsonObject().put("message", "User not found.")
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
