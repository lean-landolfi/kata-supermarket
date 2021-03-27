package service;

import com.google.inject.Inject;
import entities.RuleRequest;
import io.reactivex.Completable;
import io.reactivex.Single;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.mvel.MVELRule;
import router.MainRouter;
import storage.Neo4jClient;

import java.util.logging.Logger;

public class RulesService {

    private final Neo4jClient neo4jClient;

    private static final Logger LOGGER = Logger.getLogger(RulesService.class.getName());

    @Inject
    public RulesService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Completable setRules(RuleRequest ruleRequest) {
        return neo4jClient.createRuleNode(ruleRequest);
    }

//    public Single<Rules> getRules() {
//        Rules rules;
//        MVELRule rule = new MVELRule()
//                .name(ruleRequest.getName())
//                .description(ruleRequest.getDescription())
//                .when(ruleRequest.getCondition())
//                .then(ruleRequest.getAction());
//    }

    public Completable getPriceForListOfItems() {
        return Completable.complete();
    }
}
