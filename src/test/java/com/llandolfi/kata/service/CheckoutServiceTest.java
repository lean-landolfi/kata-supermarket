package com.llandolfi.kata.service;

import com.llandolfi.kata.entities.Item;
import com.llandolfi.kata.entities.PurchaseRequest;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckoutServiceTest extends TestCase {

    private final List<Item> mockedItemList = List.of(new Item("mocked_name_1", 1, 2),
            new Item("mocked_name_2", 2, 8));

    private final PurchaseRequest mockedPurchaseRequest = new PurchaseRequest(
            new JsonArray().add(new JsonObject(Map.of("name", "A")))
                    .add(new JsonObject(Map.of("name", "B")))
                    .add(new JsonObject(Map.of("name", "B")))
    );

    private final RulesService rulesServiceMock = mock(RulesService.class);
    private final CheckoutService checkoutService = new CheckoutService(rulesServiceMock);

    @Test
    public void getPriceForItemsReturnsAFlattenedListOfItemsWithPricesAccordingly() {
        when(rulesServiceMock.getPriceForItems(anyList())).thenReturn(Single.just(mockedItemList));

        checkoutService.getTotalAmountForItems(mockedPurchaseRequest).subscribe(
                items -> assertEquals(mockedItemList, items)
        );
    }

}