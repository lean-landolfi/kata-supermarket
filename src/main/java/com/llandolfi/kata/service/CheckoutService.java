package com.llandolfi.kata.service;

import com.google.inject.Inject;
import com.llandolfi.kata.entities.Item;
import com.llandolfi.kata.entities.PurchaseRequest;
import io.reactivex.Single;

import java.util.List;
import java.util.stream.Collectors;

import static com.llandolfi.kata.utils.Util.distinctByKey;

public class CheckoutService {

    private final RulesService rulesService;

    @Inject
    public CheckoutService(RulesService rulesService) {
        this.rulesService = rulesService;
    }

    public Single<List<Item>> getTotalAmountForItems(PurchaseRequest purchaseRequest) {
        var occurrences = purchaseRequest.getItemList().stream().collect(Collectors.groupingBy(Item::getName, Collectors.counting()));
        purchaseRequest.getItemList().forEach(item -> item.setQuantity(Math.toIntExact(occurrences.get(item.getName()))));

        List<Item> itemList = purchaseRequest.getItemList().stream()
                .filter(distinctByKey(Item::getName))
                .collect(Collectors.toList());

        return rulesService.getPriceForItems(itemList);
    }

}