package cleanTests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskStorage {

    private List<Task> systemTasks = new ArrayList<>();

    public boolean tasksAvailable() {
        return !systemTasks.isEmpty();
    }

    public TaskStorage add(Task task) {
        systemTasks.add(task);
        return this;
    }

    public TaskStorage remove(Task task) {
        systemTasks.remove(task);
        return this;
    }

    public TaskStorage clear() {
        systemTasks.clear();
        return this;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(systemTasks);
    }

    public List<Task> getTasksInGroup(String group) {
        List<Task> tasks =  systemTasks.stream()
                .filter( t -> t.group.equalsIgnoreCase(group))
                .collect(Collectors.toList());
        return tasks;
    }

}
