package entities;

public class Rule {
    private final String name;
    private final String description;
    private final String condition;
    private final String action;

    public Rule(String name, String description, String condition, String action) {
        this.name = name;
        this.description = description;
        this.condition = condition;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCondition() {
        return condition;
    }

    public String getAction() {
        return action;
    }
}
