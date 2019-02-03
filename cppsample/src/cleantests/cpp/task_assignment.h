#ifndef CPPSAMPLE_TASK_ASSIGNMENT_H
#define CPPSAMPLE_TASK_ASSIGNMENT_H
#include "executor.h"
#include "task_storage.h"
#include <map>

class TaskAssignment {
public:
    TaskAssignment(TaskStorage storage);
    Task* assignTaskIfPossible(Executor executor);
    bool availableForAssignment(Executor executor);
    bool hasTaskAssigned(Executor executor);
    bool finishCurrentTask(Executor executor);
    Task* getCurrentlyExecutedTask(Executor executor);
private:
    TaskStorage taskStorage_;
    std::map<std::string, Task> assignedTasks_;
};
#endif //CPPSAMPLE_TASK_ASSIGNMENT_H
