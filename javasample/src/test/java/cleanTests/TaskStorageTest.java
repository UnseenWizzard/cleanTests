package cleanTests;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskStorageTest {

    private TaskStorage storage;

    @Before
    public void setupEmptyStorage() {
        this.storage = new TaskStorage();
    }

    @Test
    public void taskStorageContainsTasksAfterAdd() {
        storage.add(TestTasks.GROUP_A_TASK1);
        assertThat(storage.getTasks(), hasItem(TestTasks.GROUP_A_TASK1));
    }

    @Test
    public void taskStorageEmptyAfterDeletingEverything() {
        storage.add(TestTasks.GROUP_A_TASK1).add(TestTasks.GROUP_A_TASK2);

        storage.remove(TestTasks.GROUP_A_TASK1).remove(TestTasks.GROUP_A_TASK2);

        assertThat(storage.getTasks(), empty());
    }

    @Test
    public void nothingHappensWhenRemovingUnknownTask() {
        storage.remove(TestTasks.GROUP_A_TASK1);
        assertThat(storage.getTasks(), empty());
    }

    @Test
    public void nothingHappensWhenRemovingNullTask() {
        storage.remove(null);
        assertThat(storage.getTasks(), empty());
    }

    @Test
    public void noTasksAvailableIfNoneAreAdded() {
        assertFalse("tasksAvailable() even though storage is empty!", storage.tasksAvailable());
    }

    @Test
    public void tasksAvailableAfterAdding() {
        storage.add(TestTasks.GROUP_A_TASK1);
        assertTrue("No tasksAvailable() after adding one!", storage.tasksAvailable());
    }

    @Test
    public void getTasksInGroupOnlyReturnsTasksFromSameGroup() {
        storage.add(TestTasks.GROUP_A_TASK1)
                .add(TestTasks.GROUP_A_TASK2)
                .add(TestTasks.GROUP_B_TASK1);

        assertThat(storage.getTasksInGroup(TestTasks.GROUP_A), containsInAnyOrder(TestTasks.GROUP_A_TASK1, TestTasks.GROUP_A_TASK2));
        assertThat(storage.getTasksInGroup(TestTasks.GROUP_A), not(contains(TestTasks.GROUP_B_TASK1)));
    }

}