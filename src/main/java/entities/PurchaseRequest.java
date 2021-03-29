package entities;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class PurchaseRequest {
    private final List<Item> itemList;

    public PurchaseRequest(JsonArray request) {
        this.itemList = new ArrayList<>();
        request.forEach(o -> itemList.add(new Item((JsonObject) o)));
    }

    public List<Item> getItemList() {
        return itemList;
    }
}