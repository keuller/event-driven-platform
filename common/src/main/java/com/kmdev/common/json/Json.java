package com.kmdev.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Json implements Serializable {

    public static ObjectMapper mapper = new ObjectMapper();
    public static ObjectMapper prettyMapper = new ObjectMapper();

    private final Map<String, Object> data;

    public Json() {
        data = new HashMap<>();
    }

    public Json set(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public String encode() {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }

    public String encodePretty() {
        try {
            return prettyMapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}
