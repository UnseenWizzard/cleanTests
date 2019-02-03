package cleanTests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskAssignment {

    private final TaskStorage taskStorage;
    private Map<Executor, Task> assignedTasks;

    public TaskAssignment(TaskStorage taskStorage) {
        this.taskStorage = taskStorage;
        this.assignedTasks = new HashMap<>();
    }

    public boolean assignTaskIfPossible(Executor executor) {
        if ( availableForAssignment(executor) ) {
            List<Task> availableTasks;
            Optional<String> optionalExecutorGroup = executor.getGroup();
            if (optionalExecutorGroup.isPresent()) {
                availableTasks = taskStorage.getTasksInGroup(optionalExecutorGroup.get());
            } else {
                availableTasks = taskStorage.getTasks();
            }
            if (!availableTasks.isEmpty()) {
                assignedTasks.put(executor, availableTasks.get(0));
                taskStorage.remove(availableTasks.get(0));
                return true;
            }
        }
        return false;
    }

    public boolean availableForAssignment(Executor executor) {
        return ! hasTaskAssigned(executor);
    }

    public boolean hasTaskAssigned(Executor executor) {
        return assignedTasks.containsKey(executor);
    }

    public boolean finishCurrentTask(Executor executor) {
        return assignedTasks.remove(executor) != null;
    }

    public Optional<Task> getCurrentlyExecutedTask(Executor executor) {
        return Optional.ofNullable(assignedTasks.get(executor));
    }
}
