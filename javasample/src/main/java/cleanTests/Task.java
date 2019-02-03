package cleanTests;

import java.util.Objects;

public class Task {
    public final long id;
    public final String group;

    public Task(long id, String group) {
        this.id = id;
        this.group = group;
    }

    @Override
    public String toString() {
        return String.format("Task %d (%s)", id, group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(group, task.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, group);
    }
}
