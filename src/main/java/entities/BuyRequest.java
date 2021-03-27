package entities;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class BuyRequest {
    private final List<Item> itemRequestList;

    public BuyRequest(JsonArray request) {
        this.itemRequestList = new ArrayList<>();
        request.forEach(o -> itemRequestList.add(new Item((JsonObject) o)));
    }

    public List<Item> getItemList() {
        return itemRequestList;
    }
}

class Item {
    private final String name;
    private final int quantity;
    private int price;

    public Item(JsonObject request) {
        this.name = request.getString("name");
        this.quantity = request.getInteger("quantity", 0);
        this.price = request.getInteger("price", 0);
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
}