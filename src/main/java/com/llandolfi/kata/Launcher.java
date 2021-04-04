package com.llandolfi.kata;

import io.netty.channel.DefaultChannelId;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import rx.Single;
import com.llandolfi.kata.verticles.MainVerticle;

import java.util.logging.Logger;

public class Launcher {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(Launcher.class.getName());

    public static void main(String[] args) {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        deploymentOptions.setConfig(new JsonObject().put("http.port", 8080));
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(MainVerticle.class.getName(), deploymentOptions).subscribe(res -> LOGGER.info("Verticle running with id " + res),
                error -> {
                    error.printStackTrace();
                    LOGGER.severe("Error starting !!!!!!!! " + error.getMessage());
                });
    }

    public static Single<String> start(DeploymentOptions deploymentOptions) {
        DefaultChannelId.newInstance();
        VertxOptions vertxOptions = new VertxOptions().setEventBusOptions(new EventBusOptions());

        final Vertx vertx = Vertx.vertx(vertxOptions);
        return vertx.rxDeployVerticle(MainVerticle.class.getName(), deploymentOptions);
    }
}
