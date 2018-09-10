package com.kmdev.security;

import io.vertx.core.json.JsonObject;

public class User {

    public String username = "";

    public String email = "";

    public String password = "";

    public String role = "";

    public static User build() {
        return new User();
    }

    public User fromJson(JsonObject json) {
        this.username = json.getString("username", "");
        this.password = json.getString("password", "");
        this.role = json.getString("role", "");
        this.email = json.getString("email", "");
        return this;
    }

    public JsonObject toJson() {
        return new JsonObject()
            .put("username", username)
            .put("role", role)
            .put("email", email);
    }
}
