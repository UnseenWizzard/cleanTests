package cleanTests;

import java.util.Optional;

public class Executor {

    private final String id;
    private String group;

    public Executor(String id) {
        this.id = id;
    }

    public Executor(String id, String group) {
        this.id = id;
        this.group = group;
    }

    public String getID() {
        return this.id;
    }

    public Optional<String> getGroup() {
        return Optional.ofNullable(this.group);
    }

    public Executor setGroup(String group) {
        this.group = group;
        return this;
    }

    public boolean isInGroup(String group) {
        return group.equalsIgnoreCase(this.group);
    }

}
