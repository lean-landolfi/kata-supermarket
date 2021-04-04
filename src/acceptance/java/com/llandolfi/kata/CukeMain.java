package com.llandolfi.kata;

import com.llandolfi.kata.stub.neo.Neo4jStubServer;
import cucumber.api.cli.Main;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.net.ServerSocket;

public class CukeMain {
    public static void main(String[] args) throws IOException {
        final int boltPort = getFreePort();

        new Neo4jStubServer(boltPort).start();

        startServers(boltPort);

        System.exit(passTests());
    }

    public static void startServers(int boltPort) {
        Launcher.start(new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.host", "localhost")
                        .put("http.port", 8080)
                        .put("bolt.port", boltPort)
                )).subscribe(res -> System.out.println("all good"),
                Throwable::printStackTrace);
    }

    public static int getFreePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        final int port = socket.getLocalPort();
        socket.close();
        return port;
    }

    private static byte passTests() {
        byte exitStatus = Main.run(new String[]{
                "--glue",
                "com/llandolfi", // the package which contains the glue classes
                "--plugin",
                "pretty",
                "--plugin",
                "json:target/cucumber.json",
                "classpath:kata-features/"}, Thread.currentThread().getContextClassLoader());

        System.out.println("******************************************************");
        System.out.println("********         exitStatus : " + exitStatus + "               ********");
        System.out.println("******************************************************");
        return exitStatus;
    }

}
