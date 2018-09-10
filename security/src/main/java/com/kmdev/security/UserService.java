package com.kmdev.security;

import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class UserService {

    private List<User> userList = new ArrayList<>(3);

    public UserService() {
        User user = User.build();
        user.username = "Dudu";
        user.password = "abc123";
        user.email = "dudu.magalhaes@gmail.com";
        user.role = "manager";
        userList.add(user);

        user = User.build();
        user.username = "abdoral";
        user.password = "Yt4d20V";
        user.email = "abdoral.gusmao@outlook.com";
        user.role = "admin";
        userList.add(user);
    }

    public final Function<JsonObject, JsonObject> authenticate = (json) -> {
        final String usr = json.getString("username");
        final String pwd = json.getString("password");
        Optional<User> user = userList.stream().filter(
                item -> item.username.equalsIgnoreCase(usr) && item.password.equals(pwd)
        ).findFirst();
        return (user.isPresent() ? user.get().toJson() : new JsonObject().put("message", "User not found."));
    };

    public final Function<JsonObject, JsonObject> createUser = (json) -> {
        final User user = User.build().fromJson(json);
        if ("".equals(user.email) || "".equals(user.role)) return new JsonObject().put("message", "Invalid user data.");
        userList.add(user);
        return new JsonObject().put("message", "User has been created.");
    };

}
