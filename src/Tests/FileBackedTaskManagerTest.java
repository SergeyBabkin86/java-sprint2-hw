package Tests;

import management.task.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.TaskStatus;
import utilities.Managers;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest {

    File file = new File("C:\\Users\\79268\\dev\\java-sprint2-hw\\file_Backed_TaskManager_Test.csv");

    @BeforeEach
    @Override
    public void initializeManager() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    // а) Пустой список задач
    @Test
    public void shouldNotReturnAnyTask() {
        manager.clearTaskLists();
        assertTrue(manager.getAllTasksList().isEmpty(), "Список задач не пуст");

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);
        assertTrue(loadedManager.getAllTasksList().isEmpty(), "Список задач не пуст");
    }

    // b) Эпик с пустым списком подзадач
    @Test
    public void shouldReturnEpicWithoutSubtask() {
        Epic epic = new Epic(2, "TestEpic", "Description", null);
        int epicId = manager.addEpic(epic);

        assertTrue(manager.getEpicsSubtasks(epicId).isEmpty(), "Список подзадач не пуст");

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);
        assertNotNull(loadedManager.getEpic(epicId), "Эпик не возвращен");
        assertEquals(epic, loadedManager.getEpic(epicId), "Эпики не эквивалентны");
    }

    // c) Пустой список задач
    @Test
    public void shouldNotReturnHistoryIfHistoryListIsEmpty() {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        manager.addTask(task);

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);
        assertTrue(loadedManager.history().isEmpty(), "История не пустая");
    }
}
