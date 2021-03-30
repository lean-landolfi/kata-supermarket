import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import neo.stub.Neo4jStubServer;

import java.io.IOException;
import java.net.ServerSocket;

public class CukeMain {
    public static void main(String[] args) throws IOException {
        final int boltPort = getFreePort();

        new Neo4jStubServer(boltPort).start();

        final int serverPort = getFreePort();

        startServer2(serverPort, boltPort);

    }

    public static void startServer2(int serverPort, int boltPort) {
        Launcher.start(new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.host", Constants.SERVER)
                        .put("http.port", serverPort)
                        .put("bolt.port", boltPort)
                )).blockingGet();
    }

    public static int getFreePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        final int port = socket.getLocalPort();
        socket.close();
        return port;
    }

}
