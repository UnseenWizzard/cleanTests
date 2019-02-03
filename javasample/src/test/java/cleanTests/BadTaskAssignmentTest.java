package cleanTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class BadTaskAssignmentTest {

    private TaskStorage storage = new TaskStorage();
    private TaskAssignment assignment = new TaskAssignment(storage);
    private Executor executor = new Executor("executor");

    @Before
    public void setup() {
        storage.clear();
        assignment.finishCurrentTask(executor);
    }

    @After
    public void teardown() {
        storage.clear();
    }

    @Test
    public void assignTask() {
        assertFalse(assignment.hasTaskAssigned(executor));
        assertTrue(storage.getTasks().isEmpty());

        storage.add(new Task(1, "group_A"));
        storage.add(new Task(2, "group_A"));
        storage.add(new Task(1, "group_B"));
        storage.add(new Task(42, "group_C"));

        assertTrue(assignment.assignTaskIfPossible(executor));

        Optional<Task> currentTask = assignment.getCurrentlyExecutedTask(executor);

        assertTrue(currentTask.isPresent());
        assertEquals(1, currentTask.get().id);
        assertEquals("group_A", currentTask.get().group);

        assertFalse(assignment.assignTaskIfPossible(executor));

        assignment.finishCurrentTask(executor);

        assertTrue(assignment.assignTaskIfPossible(executor));

        currentTask = assignment.getCurrentlyExecutedTask(executor);

        assertTrue(currentTask.isPresent());
        assertEquals(2, currentTask.get().id);
        assertEquals("group_A", currentTask.get().group);

        assignment.finishCurrentTask(executor);
        storage.remove(new Task(1, "group_B"));

        assertTrue(assignment.assignTaskIfPossible(executor));

        currentTask = assignment.getCurrentlyExecutedTask(executor);

        assertTrue(currentTask.isPresent());
        assertEquals(42, currentTask.get().id);
        assertEquals("group_C", currentTask.get().group);
    }

    @Test
    public void assignTaskNotPossible() {
        assertFalse(assignment.hasTaskAssigned(executor));
        assertTrue(storage.getTasks().isEmpty());

        assertFalse(assignment.assignTaskIfPossible(executor));

        storage.add(new Task(1, "group_A"));
        storage.add(new Task(2, "group_A"));
        storage.add(new Task(1, "group_B"));
        storage.add(new Task(42, "group_C"));

        assertTrue(assignment.assignTaskIfPossible(executor));

        Optional<Task> currentTask = assignment.getCurrentlyExecutedTask(executor);

        assertTrue(currentTask.isPresent());
        assertEquals(1, currentTask.get().id);
        assertEquals("group_A", currentTask.get().group);

        assertFalse(assignment.assignTaskIfPossible(executor));

        assignment.finishCurrentTask(executor);

        executor.setGroup("group_D");

        assertFalse(assignment.assignTaskIfPossible(executor));
    }

}