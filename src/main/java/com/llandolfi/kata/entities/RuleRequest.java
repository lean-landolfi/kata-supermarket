package com.llandolfi.kata.entities;

import io.vertx.core.json.JsonObject;

public class RuleRequest {
    private final String name;
    private final String affectedItem;
    private final String description;
    private final String condition;
    private final String action;

    public RuleRequest(JsonObject bodyAsJson) {
        name = bodyAsJson.getString("name");
        affectedItem = bodyAsJson.getString("affected-item");
        description = bodyAsJson.getString("description");
        condition = bodyAsJson.getString("condition");
        action = bodyAsJson.getString("action");
    }

    public String getName() {
        return name;
    }

    public String getAffectedItem() {
        return affectedItem;
    }

    public String getDescription() {
        return description;
    }

    public String getCondition() {
        return condition;
    }

    public String getAction() {
        return action;
    }
}
