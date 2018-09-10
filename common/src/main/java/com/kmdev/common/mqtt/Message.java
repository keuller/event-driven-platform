package com.kmdev.common.mqtt;

import java.util.Date;
import java.util.UUID;

public abstract class Message {

    public static final String messageId() {
        return String.format("%s-%d", UUID.randomUUID().toString().split("-")[3], new Date().getTime());
    }

    public static final String module(String module) {
        return String.format("%s-%s", module, UUID.randomUUID().toString().split("-")[3]);
    }
}
