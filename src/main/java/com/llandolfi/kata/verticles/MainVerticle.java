package com.llandolfi.kata.verticles;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import com.llandolfi.kata.modules.Module;
import com.llandolfi.kata.router.MainRouter;
import rx.Observable;

import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

    @Inject
    private MainRouter mainRouter;

    @Override
    public void start(Promise<Void> startFuture) {
        startServer().subscribe(
                t -> {
                },
                t -> LOGGER.severe(t.getMessage()),
                () -> {
                    LOGGER.info(MainVerticle.class.getName() + " Running on " + getPort() + " !!!!!!! ");
                    startFuture.complete();
                }
        );
    }

    private Observable<HttpServer> startServer() {
        Guice.createInjector(new Module(vertx)).injectMembers(this);
        HttpServerOptions options = new HttpServerOptions().setCompressionSupported(true);
        HttpServer httpServer = vertx.createHttpServer(options);
        Integer configuredPort = getPort();
        mainRouter.setRouter(Router.router(vertx));
        return httpServer.requestHandler(mainRouter.getRouter()).rxListen(configuredPort).toObservable();
    }

    private Integer getPort() {
        return this.getVertx().getOrCreateContext().config().getInteger("http.port");
    }
}
