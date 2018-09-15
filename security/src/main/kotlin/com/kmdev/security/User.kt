package com.kmdev.security

import io.vertx.core.json.JsonObject

data class User(
        var username: String = "",
        var email: String = "",
        var password: String = "",
        var role: String = "") {

    fun fromJson(json: JsonObject): User {
        this.username = json.getString("username", "")
        this.password = json.getString("password", "")
        this.role = json.getString("role", "")
        this.email = json.getString("email", "")
        return this
    }

    fun toJson(): JsonObject = JsonObject()
            .put("username", username)
            .put("role", role)
            .put("email", email)
}