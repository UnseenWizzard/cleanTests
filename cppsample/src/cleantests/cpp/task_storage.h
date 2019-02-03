#ifndef CPPSAMPLE_TASKSTORAGE_H
#define CPPSAMPLE_TASKSTORAGE_H

#include <vector>
#include <string>

struct Task {
    long id_;
    std::string group_;

    Task(long id, std::string group) : id_(id), group_(group) {}
};

class TaskStorage {
public:
    void add(Task task);
    void remove(Task task);
    void clear();
    bool tasksAvailable();
    std::vector<Task> getTasks();
    std::vector<Task> getTasksInGroup(std::string group);

private:
    std::vector<Task> systemTasks_;
};
#endif //CPPSAMPLE_TASKSTORAGE_H
