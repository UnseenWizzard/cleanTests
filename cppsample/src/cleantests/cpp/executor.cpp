#include "executor.h"

Executor::Executor(std::string id) {
    id_ = id;
}

Executor::Executor(std::string id, std::string group) {
    id_ = id;
    group_ = group;
}

std::string Executor::getGroup() {
    return group_;
}

std::string Executor::getID() {
    return id_;
}

bool Executor::isInGroup(const std::string group) {
    return group_ == group;
}