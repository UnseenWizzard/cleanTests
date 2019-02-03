#include "task_assignment.h"

TaskAssignment::TaskAssignment(TaskStorage storage) {
    taskStorage_ = storage;
}

Task* TaskAssignment::assignTaskIfPossible(Executor executor) {
    if (availableForAssignment(executor))
    {
        std::vector<Task> availableTasks;
        if (!executor.getGroup().empty())
        {
            availableTasks = taskStorage_.getTasksInGroup(executor.getGroup());
        }
        else
        {
            availableTasks = taskStorage_.getTasks();
        }
        if (!availableTasks.empty()) {
            Task nextTask = availableTasks.front();
            assignedTasks_.insert(std::pair<std::string, Task>(executor.getID(), nextTask));
            taskStorage_.remove(nextTask);
            return new Task(nextTask.id_, nextTask.group_);
        }
    }
    return NULL;
}

bool TaskAssignment::availableForAssignment(Executor executor) {
    return !hasTaskAssigned(executor);
}

bool TaskAssignment::hasTaskAssigned(Executor executor) {
    auto taskAssignedToExecutor = assignedTasks_.find(executor.getID());
    if (taskAssignedToExecutor != assignedTasks_.end())
    {
        return true;
    }
    return false;
}

bool TaskAssignment::finishCurrentTask(Executor executor) {
    for (auto it = assignedTasks_.begin(); it != assignedTasks_.end(); )
    {
        if (it->first == executor.getID())
        {
            it = assignedTasks_.erase(it);
        }
        else
        {
            ++it;
        }
    }
}

Task* TaskAssignment::getCurrentlyExecutedTask(Executor executor) {
    auto taskAssignedToExecutor = assignedTasks_.find(executor.getID());
    if (taskAssignedToExecutor != assignedTasks_.end())
    {
        return NULL;
    }
    Task* currentTask = new Task(taskAssignedToExecutor->second.id_, taskAssignedToExecutor->second.group_);
    return currentTask;
}