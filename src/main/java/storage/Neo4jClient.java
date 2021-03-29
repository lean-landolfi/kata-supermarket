package storage;

import entities.ItemRequest;
import entities.Rule;
import entities.RuleRequest;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.reactive.RxSession;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import static org.neo4j.driver.Values.parameters;

public class Neo4jClient implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(Neo4jClient.class.getName());

    private Driver driver;

    public Neo4jClient() {
        startup();
    }

    private void startup() {
        driver = GraphDatabase.driver("bolt://localhost:7687",
                AuthTokens.basic("neo4j", "password"));
    }

    public Completable createRuleNode(RuleRequest ruleRequest) {
        RxSession rxSession = driver.rxSession();

        return Completable.fromSingle(Single.fromPublisher(rxSession.run("MATCH (item:item) WHERE item.name = $affectedItem RETURN item;",
                parameters("affectedItem", ruleRequest.getAffectedItem())).records()).map(record -> {
                    if (record.size() == 0) {
                        LOGGER.severe("Affected record not found.");
                        return Completable.error(new NoSuchElementException("Item provided for rule does not exist."));
                    } else {
                        LOGGER.info("Affected record found, proceeding with rule creation.");
                        return Completable.fromPublisher(rxSession.run("MATCH (a:item {name:$itemName}) " +
                                        "MERGE (node:rule {name:$ruleName, " +
                                        "description: $description, " +
                                        "condition: $condition, " +
                                        "action: $action})-[:ON]->(a)",
                                parameters("itemName", ruleRequest.getAffectedItem(),
                                        "ruleName", ruleRequest.getName(),
                                        "description", ruleRequest.getDescription(),
                                        "condition", ruleRequest.getCondition(),
                                        "action", ruleRequest.getAction())).consume()).subscribe();
                    }
                }
                ).doOnError(throwable ->
                        LOGGER.severe(throwable.getMessage())
                ).onErrorReturn(Completable::error)
        );
    }

    public Single<List<Rule>> getRules() {
        RxSession rxSession = driver.rxSession();


        return Observable.fromPublisher(rxSession.run("MATCH (a:item)-[r]-(b) RETURN b.name, b.description, b.condition, b.action;").records()).map(record ->
                new Rule(record.get("b.name").asString(), record.get("b.description").asString(), record.get("b.condition").asString(), record.get("b.action").asString())
        ).toList();
    }

    public Completable createNode(ItemRequest itemRequest) {
        RxSession rxSession = driver.rxSession();

        return Completable.fromPublisher(rxSession.run("MERGE (node:item {name:$name})",
                parameters("name", itemRequest.getName())).consume());
    }


    @Override
    public void close() {
        driver.close();
    }
}
