package service;

import com.google.inject.Inject;
import entities.BuyRequest;
import io.reactivex.Single;

import java.math.BigDecimal;

public class CheckoutService {

    private final RulesService rulesService;

    @Inject
    public CheckoutService(RulesService rulesService) {
        this.rulesService = rulesService;
    }

    public Single<BigDecimal> getTotalAmountForItems(BuyRequest buyRequest) {
        return Single.just(BigDecimal.ONE);
    }

}