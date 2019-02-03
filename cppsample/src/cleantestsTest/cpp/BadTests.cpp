#include <gtest/gtest.h>
#include "../../cleantests/cpp/task_assignment.h"

TaskStorage storage;
TaskAssignment assignment(storage);
Executor executor("executor");

TEST(BadTaskAssignmentTest, assignTask) {
        storage.clear();
        assignment.finishCurrentTask(executor);

        storage.add(Task(1, std::string("group_A")));

        Task* assignedTask = assignment.assignTaskIfPossible(executor);
//	    EXPECT_EQ(assignedTask->id_, 1);
//		EXPECT_EQ(assignedTask->group_, std::string("group_A"));
	    storage.clear();
}

int main(int argc, char **argv) {
	  ::testing::InitGoogleTest(&argc, argv);
	    return RUN_ALL_TESTS();
}
