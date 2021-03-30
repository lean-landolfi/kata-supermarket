package neo.stub;

import org.apache.commons.io.FileUtils;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.io.File;
import java.io.IOException;

public class Neo4jStubServer {

    private final Integer boltPort;
    private final Driver driver;

    public Neo4jStubServer(Integer boltPort) {
        this.boltPort = boltPort;
        driver = GraphDatabase.driver(String.format("bolt://localhost:%s", boltPort));
    }

    public void start() {
        startBoltServer();
        new Neo4jStubLoader().loadStubData(driver);
    }

    private void startBoltServer() {
        try {
            final File neo = new File("neo");
            if (neo.exists()) {
                FileUtils.deleteDirectory(neo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        new DatabaseManagementServiceBuilder(new File("neo"))
                .setConfig(GraphDatabaseSettings.pagecache_memory, "64M")
                .setConfig(BoltConnector.enabled, true)
                .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", boltPort))
                .build();
    }
}
