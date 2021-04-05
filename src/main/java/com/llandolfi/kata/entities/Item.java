package com.llandolfi.kata.entities;

import io.vertx.core.json.JsonObject;

public class Item {
    private final String name;
    private int quantity;
    private long price;

    public Item(String name, int quantity, long price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Item(JsonObject request) {
        this.name = request.getString("name");
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
}