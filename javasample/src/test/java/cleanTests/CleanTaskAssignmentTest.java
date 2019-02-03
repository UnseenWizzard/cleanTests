package cleanTests;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CleanTaskAssignmentTest {

    private final static Executor DEFAULT_EXECUTOR = new Executor("default");
    private final static Executor GROUP_B_EXECUTOR = new Executor("B_exec", TestTasks.GROUP_B);

    private TaskStorage storage;

    private TaskAssignment assignment;

    @Before
    public void setUp() {
        storage = new TaskStorage();
        assignment = new TaskAssignment(storage);
    }

    @Test
    public void noAssignmentHappensIfThereAreNoTasks() {
        assertThat(assignment.assignTaskIfPossible(DEFAULT_EXECUTOR), equalTo(empty()));
    }

    @Test
    public void executorWithoutGroupGetsFirstAvailableTask() {
        storage.add(TestTasks.GROUP_A_TASK1).add(TestTasks.GROUP_B_TASK1);

        assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

        assertThat(assignment.getCurrentlyExecutedTask(DEFAULT_EXECUTOR), equalTo(Optional.of(TestTasks.GROUP_A_TASK1)));
    }

    @Test
    public void executorWithGroupGetsNoTaskIfNoneFromGroupAvailable() {
        storage.add(TestTasks.GROUP_A_TASK1);

        assertThat(assignment.assignTaskIfPossible(GROUP_B_EXECUTOR), equalTo(empty()));
    }

    @Test
    public void executorWithGroupGetsTaskFromGroup() {
        storage.add(TestTasks.GROUP_A_TASK1).add(TestTasks.GROUP_B_TASK1);

        assignment.assignTaskIfPossible(GROUP_B_EXECUTOR);

        assertThat(assignment.getCurrentlyExecutedTask(GROUP_B_EXECUTOR), equalTo(Optional.of(TestTasks.GROUP_B_TASK1)));
    }

    @Test
    public void executorCurrentlyExecutingATaskDoesNotGetAnother() {
        storage.add(TestTasks.GROUP_A_TASK1);

        assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

        assertThat(assignment.assignTaskIfPossible(DEFAULT_EXECUTOR), equalTo(empty()));
    }

    @Test
    public void nothingHappensWhenTaskIsFinishedForIdleExecutor() {
        assertFalse(assignment.finishCurrentTask(DEFAULT_EXECUTOR));
    }

    @Test
    public void busyExecutorIsAvailableForTasksAgainAfterFinishingCurrentTask() {
        storage.add(TestTasks.GROUP_A_TASK1);
        assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

        assertFalse(assignment.availableForAssignment(DEFAULT_EXECUTOR));

        assignment.finishCurrentTask(DEFAULT_EXECUTOR);

        assertTrue(assignment.availableForAssignment(DEFAULT_EXECUTOR));
    }
}