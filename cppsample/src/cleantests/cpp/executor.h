#ifndef CPPSAMPLE_EXECUTOR_H
#define CPPSAMPLE_EXECUTOR_H

#include <string>

class Executor {
public:
    Executor(std::string id);
    Executor(std::string id, std::string group);

    std::string getID();
    std::string getGroup();
    bool isInGroup(const std::string group);

private:
    std::string id_;
    std::string group_;
};

#endif //CPPSAMPLE_EXECUTOR_H
