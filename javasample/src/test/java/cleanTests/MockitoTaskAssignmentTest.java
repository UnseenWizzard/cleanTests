package cleanTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static cleanTests.TestTasks.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockitoTaskAssignmentTest {

    private final static Executor DEFAULT_EXECUTOR = new Executor("default");
    private final static Executor GROUP_B_EXECUTOR = new Executor("B_exec", GROUP_B);

    @Mock
    private TaskStorage storage;

    private TaskAssignment assignment;

    @Before
    public void setUp() {
        assignment = new TaskAssignment(storage);
    }

    @Test
    public void noAssignmentHappensIfThereAreNoTasks() {
        assertFalse(assignment.assignTaskIfPossible(DEFAULT_EXECUTOR));
    }

    @Test
    public void executorWithoutGroupGetsFirstAvailableTask() {
        when(storage.getTasks()).thenReturn(List.of(GROUP_A_TASK1, GROUP_B_TASK1));

        assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

        assertThat(assignment.getCurrentlyExecutedTask(DEFAULT_EXECUTOR), equalTo(Optional.of(GROUP_A_TASK1)));
    }

    @Test
    public void executorWithGroupGetsNoTaskIfNoneFromGroupAvailable() {
        when(storage.getTasksInGroup(GROUP_B)).thenReturn(Collections.emptyList());

        assertFalse(assignment.assignTaskIfPossible(GROUP_B_EXECUTOR));
    }

    @Test
    public void executorWithGroupGetsTaskFromGroup() {
        when(storage.getTasksInGroup(GROUP_B)).thenReturn(Collections.singletonList(GROUP_B_TASK1));

        assignment.assignTaskIfPossible(GROUP_B_EXECUTOR);

        assertThat(assignment.getCurrentlyExecutedTask(GROUP_B_EXECUTOR), equalTo(Optional.of(GROUP_B_TASK1)));
    }

    @Test
    public void executorCurrentlyExecutingATaskDoesNotGetAnother() {
        when(storage.getTasks()).thenReturn(List.of(GROUP_A_TASK1));

        assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

        assertFalse(assignment.assignTaskIfPossible(DEFAULT_EXECUTOR));
    }

    @Test
    public void nothingHappensWhenTaskIsFinishedForIdleExecutor() {
        assertFalse(assignment.finishCurrentTask(DEFAULT_EXECUTOR));
    }

    @Test
    public void busyExecutorIsAvailableForTasksAgainAfterFinishingCurrentTask() {
        when(storage.getTasks()).thenReturn(List.of(GROUP_A_TASK1));
        assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

        assertFalse(assignment.availableForAssignment(DEFAULT_EXECUTOR));

        assignment.finishCurrentTask(DEFAULT_EXECUTOR);

        assertTrue(assignment.availableForAssignment(DEFAULT_EXECUTOR));
    }
}