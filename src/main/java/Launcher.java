import io.netty.channel.DefaultChannelId;
import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import verticles.MainVerticle;

import java.util.logging.Logger;

public class Launcher {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(Launcher.class.getName());

    public static void main(String[] args) {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        deploymentOptions.setConfig(new JsonObject().put("http.port", 8080));
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(MainVerticle.class.getName(), deploymentOptions);
    }

    public static Single<String> start(DeploymentOptions deploymentOptions) {
        DefaultChannelId.newInstance();
        VertxOptions vertxOptions = new VertxOptions().setEventBusOptions(getEventBusOptions());
        vertxOptions.setEventLoopPoolSize(5);
        vertxOptions.setWorkerPoolSize(1);
        vertxOptions.setInternalBlockingPoolSize(1);
        return run(MainVerticle.class, vertxOptions, deploymentOptions).map(id -> {
            LOGGER.info("Main verticle ready and started <<<<<<<<<<<<<<<<<<<<<< " + id);
            return id;
        });
    }

    private static EventBusOptions getEventBusOptions() {
        var eventBusOptions = new EventBusOptions();
        var clusterEnable = Boolean.parseBoolean(System.getenv("CLUSTER_ENABLE"));
        if (clusterEnable) {
            var podIp = System.getenv("MY_POD_IP");
            eventBusOptions.setHost(podIp);
            LOGGER.info(String.format("POD IP %s", podIp));
        }
        return eventBusOptions;
    }

    private static Single<String> run(Class<? extends AbstractVerticle> verticle, VertxOptions options,
                                      DeploymentOptions deploymentOptions) {
        final Vertx vertx = Vertx.vertx(options);
        return vertx.rxDeployVerticle(verticle.getName(), deploymentOptions);
    }
}
