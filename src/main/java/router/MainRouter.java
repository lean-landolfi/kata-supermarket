package router;

import com.google.inject.Inject;
import entities.ItemRequest;
import entities.RuleRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.ResponseTimeHandler;
import service.RulesService;
import storage.Neo4jClient;

import java.util.logging.Logger;

public class MainRouter {
    private static final Logger LOGGER = Logger.getLogger(MainRouter.class.getName());

    private Router router;

    @Inject
    private Neo4jClient neo4jClient;

    @Inject
    private RulesService rulesService;

    public Router getRouter() {

        router.route().handler(ResponseTimeHandler.create()).handler(BodyHandler.create());

        router.get("/health-check").handler(routingContext ->
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(new JsonObject().put("msg", "Ok").encode())
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
                        () -> routingContext.response().end(),
                        throwable -> routingContext.response().setStatusCode(500).end(throwable.getMessage())
                )
        );

        router.post("/buy").handler(routingContext ->
                routingContext.response().putHeader("content-type", "application/json")
                        .end(new JsonObject(routingContext.getBodyAsString()).encode())
        );

        LOGGER.fine("Routing Done");

        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }


}
