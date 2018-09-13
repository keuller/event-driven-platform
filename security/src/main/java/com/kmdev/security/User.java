package com.kmdev.security;

import io.vertx.core.json.JsonObject;

/**
 * User class is Pojo.
 */
public class User {

    String username = "";

    String email = "";

    String password = "";

    String role = "";

    static User build() {
        return new User();
    }

    User fromJson(JsonObject json) {
        this.username = json.getString("username", "");
        this.password = json.getString("password", "");
        this.role = json.getString("role", "");
        this.email = json.getString("email", "");
        return this;
    }

    JsonObject toJson() {
        return new JsonObject()
            .put("username", username)
            .put("role", role)
            .put("email", email);
    }
}
