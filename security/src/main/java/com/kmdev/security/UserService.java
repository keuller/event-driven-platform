package com.kmdev.security;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import static java.util.Objects.requireNonNull;

/**
 * User service class provides business functions to handle users.
 *
 * @author keuller.magalhaes at gmail.com
 */
public final class UserService {

    private List<User> userList = new ArrayList<>(2);

    UserService() {
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

    final Function<JsonObject, JsonObject> authenticate = (json) -> {
        requireNonNull(json);
        final String usr = json.getString("username");
        final String pwd = json.getString("password");
        Optional<User> user = userList.stream().filter(
            item -> item.username.equalsIgnoreCase(usr) && item.password.equals(pwd)
        ).findFirst();
        return (user.isPresent() ? user.get().toJson() : new JsonObject().put("message", "User not found."));
    };

    final Function<JsonObject, JsonObject> createUser = (json) -> {
        requireNonNull(json);
        final User user = User.build().fromJson(json);
        if ("".equals(user.email) || "".equals(user.role)) return new JsonObject().put("message", "Invalid user data.");
        userList.add(user);
        return new JsonObject().put("message", "User has been created.");
    };

    final Function<String, JsonObject> findByEmail = (mail) -> {
        requireNonNull(mail);
        Optional<User> user = userList.stream().filter(item -> item.email.equalsIgnoreCase(mail)).findFirst();
        return (user.isPresent() ? user.get().toJson() : new JsonObject().put("message", "User not found."));
    };

    final Supplier<JsonObject> findAll = () -> {
        final JsonArray users = new JsonArray();
        userList.stream().forEach(user -> users.add(user.toJson()));
        return new JsonObject().put("users", users);
    };

}
