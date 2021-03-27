import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import verticles.MainVerticle;

public class Launcher {
    public static void main(String[] args) {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        deploymentOptions.setConfig(new JsonObject().put("http.port", 8080));
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), deploymentOptions);
    }
}
