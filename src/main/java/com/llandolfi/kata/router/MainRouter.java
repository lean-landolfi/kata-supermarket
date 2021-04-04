package com.llandolfi.kata.router;

import com.google.inject.Inject;
import com.llandolfi.kata.entities.ItemRequest;
import com.llandolfi.kata.entities.PurchaseRequest;
import com.llandolfi.kata.entities.RuleRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.ResponseTimeHandler;
import com.llandolfi.kata.service.CheckoutService;
import com.llandolfi.kata.service.RulesService;
import com.llandolfi.kata.storage.Neo4jClient;
import org.eclipse.jetty.http.HttpStatus;

import java.util.logging.Logger;

public class MainRouter {
    private static final Logger LOGGER = Logger.getLogger(MainRouter.class.getName());

    private Router router;

    @Inject
    private Neo4jClient neo4jClient;

    @Inject
    private RulesService rulesService;

    @Inject
    private CheckoutService checkoutService;

    public Router getRouter() {

        router.route().handler(ResponseTimeHandler.create()).handler(BodyHandler.create());
        router.route().failureHandler(failureRoutingContext -> {

            LOGGER.severe(failureRoutingContext.failure().getMessage());

            int statusCode = failureRoutingContext.statusCode();

            HttpServerResponse response = failureRoutingContext.response();
            response.setStatusCode(statusCode).end("Sorry! Not today");

        });
        router.get("/health-check").handler(routingContext ->
                routingContext.response()
                        .putHeader("content-type", "application/json").setStatusCode(HttpStatus.OK_200)
                        .end(new JsonObject().put("msg", "OK").encode())
        );

        router.post("/create-item").handler(routingContext ->
                neo4jClient.createNode(new ItemRequest(routingContext.getBodyAsJson()))
                        .doOnError(
                                throwable -> routingContext.response().setStatusCode(500).end(throwable.getMessage())
                        ).subscribe(
                        () -> routingContext.response().end()
                )
        );

        router.post("/rule").handler(routingContext ->
                rulesService.setRules(new RuleRequest(routingContext.getBodyAsJson())).subscribe(
                        () -> routingContext.response().end(new JsonObject().put("msg", "Ok").encode()),
                        throwable -> routingContext.response().setStatusCode(500).end(throwable.getMessage())
                )
        );

        router.post("/purchase").handler(routingContext ->
                checkoutService.getTotalAmountForItems(new PurchaseRequest(routingContext.getBodyAsJsonArray())).subscribe(
                        itemList ->
                                routingContext.response().putHeader("content-type", "application/json")
                                        .end(new JsonArray(itemList).encode()),
                        throwable -> routingContext.response().setStatusCode(500).end(throwable.getMessage())
                )
        );

        LOGGER.fine("Routing Done");

        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }


}
