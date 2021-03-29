package service;

import com.google.inject.Inject;
import entities.Item;
import entities.Rule;
import entities.RuleRequest;
import io.reactivex.Completable;
import io.reactivex.Single;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import storage.Neo4jClient;

import java.util.List;

public class RulesService {

    private final Neo4jClient neo4jClient;
    private final RulesEngine rulesEngine = new DefaultRulesEngine();

    @Inject
    public RulesService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Completable setRules(RuleRequest ruleRequest) {
        return neo4jClient.createRuleNode(ruleRequest);
    }

    public Single<List<Rule>> getRules() {
        return neo4jClient.getRules();
    }

    public Single<List<Item>> getPriceForItems(List<Item> items) {
       return getRulesForItemList().map(
                rules -> executeRulesForItemList(items, rules)
        );
    }

    public Single<List<Rule>> getRulesForItemList() {
        return getRules();
    }

    public List<Item> executeRulesForItemList(List<Item> items, List<Rule> rules) {
        Rules ruleList = new Rules();
        rules.forEach(rule ->
                ruleList.register(new MVELRule()
                        .name(rule.getName())
                        .description(rule.getDescription())
                        .when(rule.getCondition())
                        .then(rule.getAction()))
        );

        items.forEach(item ->
                {
                    Facts facts = new Facts();
                    facts.put("item", item);

                    rulesEngine.fire(ruleList, facts);
                }
        );
        return items;
    }
}
