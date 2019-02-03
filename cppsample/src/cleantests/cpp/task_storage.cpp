#include "task_storage.h"

void TaskStorage::add(Task task) {
    systemTasks_.push_back(task);
}

void TaskStorage::remove(Task task) {
    std::vector<Task>::iterator it = systemTasks_.begin();
    while (it != systemTasks_.end())
    {
        if (it->id_ == task.id_ && it->group_ == task.group_)
        {
            systemTasks_.erase(it++);
        }
        else
        {
            ++it;
        }
    }
}

void TaskStorage::clear() {
    systemTasks_.clear();
}

bool TaskStorage::tasksAvailable() {
    return !systemTasks_.empty();
}

std::vector<Task> TaskStorage::getTasks() {
    return systemTasks_;
}

std::vector<Task> TaskStorage::getTasksInGroup(std::string group) {
    std::vector<Task> tasksInGroup;
    std::vector<Task>::iterator it = systemTasks_.begin();
    while (it != systemTasks_.end())
    {
        if (it->group_ == group)
        {
            tasksInGroup.push_back(*it);
        }
    }
    return tasksInGroup;
}