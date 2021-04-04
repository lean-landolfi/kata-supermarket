package com.llandolfi.kata.entities;

import io.vertx.core.json.JsonObject;

public class ItemRequest {
    private final String name;

    public ItemRequest(JsonObject request) {
        this.name = request.getString("name");
    }

    public String getName() {
        return name;
    }
}