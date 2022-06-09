package Tests;

import management.task.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilities.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract public class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    @BeforeEach
    public abstract void initializeManager();

    // 2.1 Получение списка всех задач.
    @Test
    public void shouldReturnTaskList() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        final int taskId = manager.addTask(task);
        final Task savedTask = manager.getTask(taskId);

        Epic epic = new Epic(2, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(3, epicId, "TestSubtask", "Description", TaskStatus.NEW);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        List<Task> taskList = manager.getAllTasksList();

        assertNotNull(taskList, "TaskList is empty");
        assertTrue(taskList.contains(savedTask), "TaskList doesn't contain savedTask");
        assertTrue(taskList.contains(savedEpic), "TaskList doesn't contain savedEpic");
        assertTrue(taskList.contains(savedSubtask), "TaskList doesn't contain savedSubtask");
    }

    @Test
    public void shouldReturnPrioritizedTasks() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        manager.addTask(task);

        Task task1 = new Task(2, "TestTask", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2022, 4, 11, 13, 45));
        task1.setDuration(Duration.ofHours(10));
        manager.addTask(task1);

        Task task2 = new Task(3, "TestTask", "Description", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2022, 4, 2, 13, 45));
        task2.setDuration(Duration.ofHours(10));
        manager.addTask(task2);

        Epic epic = new Epic(4, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(5, epicId, "TestSubtask", "Description", TaskStatus.NEW);
        subtask.setStartTime(LocalDateTime.of(2022, 4, 1, 13, 45));
        subtask.setDuration(Duration.ofHours(10));
        manager.addSubtask(subtask);

        Subtask subtask1 = new Subtask(6, epicId, "TestSubtask", "Description", TaskStatus.NEW);
        subtask1.setStartTime(LocalDateTime.of(2022, 4, 3, 13, 45));
        subtask1.setDuration(Duration.ofHours(10));
        manager.addSubtask(subtask1);

        Set<Task> taskSet = manager.getPrioritizedTasks();

        assertEquals(6, taskSet.size(), "Not all tasks has been added");
        Object[] taskArray = taskSet.toArray();
        assertEquals(epic, taskArray[0], "Sequence is incorrect");
        assertEquals(subtask, taskArray[1], "Sequence is incorrect");
        assertEquals(task2, taskArray[2], "Sequence is incorrect");
        assertEquals(subtask1, taskArray[3], "Sequence is incorrect");
        assertEquals(task1, taskArray[4], "Sequence is incorrect");
        assertEquals(task, taskArray[5], "Sequence is incorrect");
    }

    // 2.2 Удаление всех задач.
    @Test
    public void shouldReturnNullIfAllTaskCleared() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        final int taskId = manager.addTask(task);
        final Task savedTask = manager.getTask(taskId);

        Epic epic = new Epic(2, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(3, epicId, "TestSubtask", "Description", TaskStatus.NEW);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        List<Task> taskList = manager.getAllTasksList();

        assertNotNull(taskList, "TaskList is empty");
        assertTrue(taskList.contains(savedTask), "TaskList doesn't contain savedTask");
        assertTrue(taskList.contains(savedEpic), "TaskList doesn't contain savedEpic");
        assertTrue(taskList.contains(savedSubtask), "TaskList doesn't contain savedSubtask");

        manager.clearTaskLists();
        List<Task> taskList1 = manager.getAllTasksList();
        assertTrue(taskList1.isEmpty(), "TaskList is notEmpty");

        IOException ex = assertThrows(IOException.class, () -> manager.getTask(taskId));
        assertEquals(String.format("Задачи с ID: %s не существует", taskId), ex.getMessage());

        IOException ex1 = assertThrows(IOException.class, () -> manager.getEpic(epicId));
        assertEquals(String.format("Эпика с ID: %s не существует", 2), ex1.getMessage());

        IOException ex3 = assertThrows(IOException.class, () -> manager.getSubtask(subtaskId));
        assertEquals(String.format("Сабтаска с ID: %s не существует", subtaskId), ex3.getMessage());
    }

    // 2.3.1 Получение задачи по идентификатору.
    // а) Стандартные условия (Id корректный)
    @Test
    public void shouldReturnTask() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        final int taskId = manager.addTask(task);
        final Task savedTask = manager.getTask(taskId);

        Task returnedTask = manager.getTask(taskId);
        assertNotNull(returnedTask, "Task is null");
        assertEquals(returnedTask, savedTask, "Tasks are not equal");
        assertTrue(manager.history().contains(savedTask),
                "Object was not added to history"); //Добавился ли в историю?
    }

    // b) Не верный Id (таска нет в списке)
    @Test
    public void shouldNotReturnTask() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        manager.addTask(task);

        IOException ex = assertThrows(IOException.class, () -> manager.getTask(2));
        assertEquals(String.format("Задачи с ID: %s не существует", 2), ex.getMessage());

        assertTrue(manager.history().isEmpty(), "History is not empty"); // Добавился ли в историю?
    }

    // 2.3.2 Получение Epic по идентификатору.
    // а) Стандартные условия (Id корректный)
    @Test
    public void shouldReturnEpic() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Epic returnedEpic = manager.getEpic(epicId);
        assertNotNull(returnedEpic, "Epic is null");
        assertEquals(returnedEpic, savedEpic, "Epics are not equal");

        assertTrue(manager.history().contains(savedEpic),
                "Object was not added to history"); //Добавился ли в историю?
    }

    // b) Не верный Id (Epic нет в списке)
    @Test
    public void shouldNotReturnEpic() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        manager.addEpic(epic);

        IOException ex = assertThrows(IOException.class, () -> manager.getEpic(2));
        assertEquals(String.format("Эпика с ID: %s не существует", 2), ex.getMessage());

        assertTrue(manager.history().isEmpty(), "History is not empty"); // Добавился ли в историю?
    }

    // 2.3.3 Получение Subtask по идентификатору.
    // а) Стандартные условия (Id корректный)
    @Test
    public void shouldReturnSubtask() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.NEW);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        Subtask returnedSubtask = manager.getSubtask(subtaskId);
        assertNotNull(returnedSubtask, "Subtask is null");
        assertEquals(returnedSubtask, savedSubtask, "Subtasks are not equal");

        assertTrue(manager.history().contains(savedEpic),
                "Epic was not added to history"); //Добавился ли в историю?
        assertTrue(manager.history().contains(savedSubtask),
                "Subtask was not added to history"); //Добавился ли в историю?
    }

    // b) Не верный Id (Subtask нет в списке)
    @Test
    public void shouldNotReturnSubtask() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask);

        IOException ex = assertThrows(IOException.class, () -> manager.getSubtask(3));
        assertEquals(String.format("Сабтаска с ID: %s не существует", 3), ex.getMessage());

        assertTrue(manager.history().isEmpty(), "History is not empty"); // Добавился ли в историю?
    }

    // 2.4.1 Создание Task
    // a) Создание Task (нет других Task в списке)
    @Test
    public void shouldAddTaskIfNoAnotherTasks() throws IOException {
        Task testTask = new Task("TestTask", "TestDescription", TaskStatus.NEW);
        final int taskId = manager.addTask(testTask);
        final Task savedTask = manager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(testTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(testTask, tasks.get(0), "Задачи не совпадают.");
    }

    // b) Создание Task (Есть другие Task в списке)
    @Test
    public void shouldAddTaskIfAnotherTasksArePresent() throws IOException {
        Task testTask = new Task("TestTask", "Description", TaskStatus.NEW);
        manager.addTask(testTask);

        Task testTask1 = new Task(2, "TestTask1", "Description", TaskStatus.NEW);
        final int taskId1 = manager.addTask(testTask1);
        final Task savedTask1 = manager.getTask(taskId1);

        assertNotNull(savedTask1, "Task не найдена.");
        assertEquals(testTask1, savedTask1, "Task не совпадают.");

        final List<Task> tasksList = manager.getAllTasksList();

        assertNotNull(tasksList, "Task не возвращаются.");
        assertEquals(2, tasksList.size(), "Неверное количество задач.");
        assertEquals(testTask, tasksList.get(0), "Task не совпадают.");
        assertEquals(testTask1, tasksList.get(1), "Task не совпадают.");
    }

    // c) Создание Task (попытка добавить Task с существующим Id)
    @Test
    public void shouldNotAddTaskWithSameId() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        manager.addTask(task);

        Task task1 = new Task(1, "TestTask1", "Description", TaskStatus.IN_PROGRESS);

        IOException ex = assertThrows(IOException.class, () -> manager.addTask(task1));
        assertEquals(String.format("Невозможно добавить задачу. " +
                "Задача с ID: %s уже существует", task1.getId()), ex.getMessage());

        final List<Task> tasksList = manager.getAllTasksList();

        assertNotNull(tasksList, "Task не возвращаются.");
        assertEquals(1, tasksList.size(), "Неверное количество Task.");
        assertEquals(task, tasksList.get(0), "Task не совпадают.");
    }

    // 2.4.2 Создание Epic
    // a) Создание Epic (нет других Epic в списке)
    @Test
    public void shouldAddEpicIfNoAnotherTasks() throws IOException {
        Epic epic = new Epic("TestEpic", "TestDescription1", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Task> epicsList = manager.getAllTasksList();

        assertNotNull(epicsList, "Эпики не возвращаются.");
        assertEquals(1, epicsList.size(), "Неверное количество эпиков.");
        assertEquals(savedEpic, epicsList.get(0), "Эпики нет в списке.");
    }

    // b) Создание Epic (Есть другие Epic в списке)
    @Test
    public void shouldAddEpicIfAnotherEpicsArePresent() throws IOException {
        Epic epic = new Epic("TestEpic", "Description", null);
        manager.addEpic(epic);

        Epic epic1 = new Epic(2, "TestEpic1", "Description", null);
        final int taskId1 = manager.addEpic(epic1);
        final Epic savedEpic1 = manager.getEpic(taskId1);

        assertNotNull(savedEpic1, "Epic не найдена.");
        assertEquals(epic1, savedEpic1, "Epic не совпадают.");

        final List<Task> epicList = manager.getAllTasksList();

        assertNotNull(epicList, "Epic не возвращаются.");
        assertEquals(2, epicList.size(), "Неверное количество Epic.");
        assertEquals(epic, epicList.get(0), "Epic не совпадают.");
        assertEquals(epic1, epicList.get(1), "Epic не совпадают.");
    }

    // c) Создание Epic (попытка добавить Epic с существующим Id)
    @Test
    public void shouldNotAddEpicWithSameId() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        manager.addEpic(epic);

        Epic epic1 = new Epic(1, "TestEpic1", "Description", null);

        IOException ex = assertThrows(IOException.class, () -> manager.addEpic(epic1));
        assertEquals(String.format("Невозможно добавить эпик. " +
                "Эпик с ID: %s уже существует", epic1.getId()), ex.getMessage());

        final List<Task> epicList = manager.getAllTasksList();

        assertNotNull(epicList, "Epic не возвращаются.");
        assertEquals(1, epicList.size(), "Неверное количество Epic.");
        assertEquals(epic, epicList.get(0), "Epic не совпадают.");
    }

    // 2.4.3. Создание Subtask
    // a) Создание Subtask (нет других Subtask в списке)
    @Test
    public void shouldAddSubtaskIfNoAnotherSubtasks() throws IOException {
        Epic epic = new Epic("TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedEpic, "Epic не найден.");

        Subtask testSubtask = new Subtask(epicId, "TestSubtask", "Description", TaskStatus.NEW);
        final int subtaskId = manager.addSubtask(testSubtask);
        final Subtask savedTestSubtask = manager.getSubtask(subtaskId);

        assertNotNull(savedTestSubtask, "Subtask не найден.");
        assertEquals(testSubtask, savedTestSubtask, "Epic не совпадают.");

        final List<Task> subtaskAndEpicList = manager.getAllTasksList();

        assertNotNull(subtaskAndEpicList, "subtasksAndEpic не возвращаются.");
        assertEquals(2, subtaskAndEpicList.size(), "Неверное количество задач в списке.");
        assertEquals(savedEpic, subtaskAndEpicList.get(0), "Epic нет в списке.");
        assertEquals(savedTestSubtask, subtaskAndEpicList.get(1), "Subtask нет в списке.");
    }

    // b) Создание Subtask (Есть другие Subtask в списке)
    @Test
    public void shouldAddSubtaskIfAnotherSubtasksArePresent() throws IOException {
        Epic epic = new Epic("TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedEpic, "Epic не найден.");

        Subtask testSubtask = new Subtask(epicId, "TestSubtask", "Description", TaskStatus.NEW);
        final int subtaskId = manager.addSubtask(testSubtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Subtask не найден.");
        assertEquals(testSubtask, savedSubtask, "Epic не совпадают.");

        final List<Task> subtaskAndEpicList = manager.getAllTasksList();

        assertNotNull(subtaskAndEpicList, "subtasksAndEpic не возвращаются.");
        assertEquals(2, subtaskAndEpicList.size(), "Неверное количество задач в списке.");
        assertEquals(savedSubtask, subtaskAndEpicList.get(1), "Subtask нет в списке.");
    }

    // c) Создание Subtask (попытка добавить Subtask с существующим Id)
    @Test
    public void shouldNotAddSubtaskWithSameId() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedEpic, "Epic не найден.");

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask);

        Subtask subtask1 = new Subtask(2, epicId, "TestSubtask1", "Description", TaskStatus.IN_PROGRESS);

        IOException ex = assertThrows(IOException.class, () -> manager.addSubtask(subtask1));
        assertEquals(String.format("Невозможно добавить подзадачу. " +
                "Подзадача с ID: %s уже существует", subtask1.getId()), ex.getMessage());

        final List<Task> subtaskAndEpicList = manager.getAllTasksList();

        assertNotNull(subtaskAndEpicList, "subtasksAndEpic не возвращаются.");
        assertEquals(2, subtaskAndEpicList.size(), "Неверное количество Subtask.");
        assertEquals(subtask, subtaskAndEpicList.get(1), "Subtask не совпадают.");
    }

    // d) Проверка изменения статуса и времени Epic при добавлении Subtask
    @Test
    public void shouldChangeEpicStatus() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        assertEquals(TaskStatus.NEW, savedEpic.getStatus());
        Subtask subtask = new Subtask(epicId, "TestSubtask", "Description", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Не верный статус Epic");
    }

    // e) Проверка времени Epic при добавлении Subtask
    @Test
    public void shouldChangeEpicTime() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        assertNull(savedEpic.getStartTime(), "Время начала Epic установлено");
        assertNull(savedEpic.getDuration(), "Продолжительность Epic установлена");
        assertNull(savedEpic.getEndTime(), "Время окончания Epic установлено");

        Subtask testSubtask1 = new Subtask(epicId, "TestSubtask1", "Description", TaskStatus.IN_PROGRESS);
        testSubtask1.setStartTime(LocalDateTime.of(2022, 4, 16, 13, 45));
        testSubtask1.setDuration(Duration.ofDays(10));
        manager.addSubtask(testSubtask1);

        assertEquals(LocalDateTime.of(2022, 4, 16, 13, 45),
                savedEpic.getStartTime(),
                "Epic's startTime was not changed");
        assertEquals(Duration.ofDays(10),
                savedEpic.getDuration(), "Epic's duration was not changed");
        assertEquals(LocalDateTime.of(2022, 4, 26, 13, 45),
                savedEpic.getEndTime(),
                "Epic endTime was not changed");
    }

    // e) Проверка наличия Epic, привязанного к Subtask;
    @Test
    public void shouldReturnEpicLinkedToSubtask() throws IOException {
        final int epicId = manager.addEpic(new Epic("TestEpic", "Description", null));
        final Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(savedEpic, "Epic не найден.");

        Subtask subtask = new Subtask(epicId, "TestSubtask", "Description", TaskStatus.NEW);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        int returnedEpicId1 = savedSubtask.getEpicId();

        assertNotNull(manager.getEpic(returnedEpicId1), "Epic is null");
    }

    // 2.5.1 Обновление Tas
    // a) Обновление Task (нормальные условия)
    @Test
    public void shouldUpdateTask() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        final int taskId = manager.addTask(task);
        final Task savedTask = manager.getTask(taskId);

        Task task1 = new Task(1, "TestTask1", "Description", TaskStatus.IN_PROGRESS);
        assertNotEquals(savedTask, task1, "Same tasks");

        manager.updateTask(task1);
        assertNotNull(manager.getTask(1), "Task is not exist");
        assertEquals(task1, manager.getTask(1), "Tasks are not equals");
    }

    // b) Обновление task (task нет в списке или у task нет Id)
    @Test
    public void shouldNotUpdateTaskWithoutIdOrNotExistTask() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        final int taskId = manager.addTask(task);
        final Task savedTask = manager.getTask(taskId);

        // 1. Попытка обновить Task без указания Id
        Task task1 = new Task("TestTask1", "Description", TaskStatus.IN_PROGRESS);
        assertEquals(savedTask, manager.getTask(taskId), "Задачи не равны");

        IOException ex = assertThrows(IOException.class, () -> manager.updateTask(task1));
        assertEquals("Невозможно обновить задачу: не указан ID || задача отсутствует.", ex.getMessage());

        List<Task> tasksList = manager.getAllTasksList();
        assertEquals(1, tasksList.size(), "Неверное количество задач.");

        // 2. Попытка обновить не существующий Task (нет такого Id)
        Task task2 = new Task(3, "TestTask2", "Description", TaskStatus.NEW);
        IOException ex1 = assertThrows(IOException.class, () -> manager.getTask(3));
        assertEquals(String.format("Задачи с ID: %s не существует", 3), ex1.getMessage());

        IOException ex2 = assertThrows(IOException.class, () -> manager.updateTask(task2));
        assertEquals("Невозможно обновить задачу: не указан ID || задача отсутствует.", ex2.getMessage());

        List<Task> tasksList1 = manager.getAllTasksList();
        assertEquals(1, tasksList1.size(), "Неверное количество задач.");
    }

    // 2.5.2 Обновление Epic
    // a1) Нормальные условия (нет привязанных Subtask)
    @Test
    public void shouldUpdateEpic() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Epic epic1 = new Epic(1, "TestEpic1", "Description", null);
        assertNotEquals(savedEpic, epic1, "Same Epics");

        manager.updateEpic(epic1);
        assertNotNull(manager.getEpic(1), "Epic is not exist");
        assertNotEquals(epic, manager.getEpic(1), "Epic are equal");
    }

    // a2. есть привязанные подзадачи
    @Test
    public void shouldUpdateEpicWithLinkedSubtasks() throws IOException {
        Epic epic = new Epic(1, "TestEpic1", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        List<Subtask> linkedSubtaskList = manager.getEpicsSubtasks(1);
        assertEquals(1, linkedSubtaskList.size(), "Неверное количество Subtask.");
        assertEquals(linkedSubtaskList.get(0), savedSubtask, "Subtask не равны");

        Epic epic1 = new Epic(1, "TestEpic2", "Description", null);
        assertNotEquals(savedEpic, epic1, "Same Epics");

        manager.updateEpic(epic1);
        assertNotNull(manager.getEpic(1), "Epic is not exist");
        assertNotEquals(epic, manager.getEpic(1), "Epics are equal");

        List<Subtask> linkedSubtaskList1 = manager.getEpicsSubtasks(1);
        assertEquals(linkedSubtaskList.get(0), linkedSubtaskList1.get(0), "Linked subtask are not equals");
    }

    // b) Обновление Epic (Epic нет в списке или у Epic нет Id)
    @Test
    public void shouldNotUpdateEpicWithoutIdOrNotExistEpic() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        // 1. Попытка обновить Epic без указания Id
        Epic epic1 = new Epic("TestEpic1", "Description", null);
        assertEquals(savedEpic, manager.getEpic(epicId), "Epic не равны");

        IOException ex = assertThrows(IOException.class, () -> manager.updateEpic(epic1));
        assertEquals("Невозможно обновить эпик: не указан ID || эпик отсутствует.", ex.getMessage());

        List<Task> epicList = manager.getAllTasksList();
        assertEquals(1, epicList.size(), "Неверное количество Epic.");

        // 2. Попытка обновить не существующую задачу (нет такого Id)
        Epic epic2 = new Epic(3, "TestEpic2", "Description", TaskStatus.NEW);
        IOException ex1 = assertThrows(IOException.class, () -> manager.getEpic(3));
        assertEquals(String.format("Эпика с ID: %s не существует", 3), ex1.getMessage());

        IOException ex2 = assertThrows(IOException.class, () -> manager.updateEpic(epic2));
        assertEquals("Невозможно обновить эпик: не указан ID || эпик отсутствует.", ex2.getMessage());

        List<Task> tasksList1 = manager.getAllTasksList();
        assertEquals(1, tasksList1.size(), "Неверное количество Epic.");
    }

    // 2.5.3 Обновление Subtask
    // a) Нормальные условия
    @Test
    public void shouldUpdateSubtask() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        Subtask subtask1 = new Subtask(2, epicId, "TestSubtask1", "Description", TaskStatus.DONE);
        assertNotEquals(savedSubtask, subtask1, "Subtask равны");

        manager.updateSubtask(subtask1);
        assertNotNull(manager.getSubtask(2), "Subtask is not exist");
        assertNotEquals(savedSubtask, manager.getSubtask(2), "Subtask are equal");
        assertEquals(subtask1, manager.getSubtask(2), "Subtask are not equal");
    }

    // b) Обновление Epic (Epic нет в списке или у Epic нет Id)
    @Test
    public void shouldNotUpdateSubtaskWithoutIdOrNotExistSubtask() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        // 1. Попытка обновить Epic без указания Id
        Subtask subtask1 = new Subtask(epicId, "TestSubtask1", "Description", TaskStatus.IN_PROGRESS);
        assertEquals(savedSubtask, manager.getSubtask(subtaskId), "Subtask не равны");

        IOException ex = assertThrows(IOException.class, () -> manager.updateSubtask(subtask1));
        assertEquals("Невозможно обновить подзадачу: не указан ID || подзадача отсутствует.", ex.getMessage());

        List<Task> subtaskAndEpicList = manager.getAllTasksList();
        assertEquals(2, subtaskAndEpicList.size(), "Неверное количество Subtask & Epic.");

        // 2. Попытка обновить не существующую задачу (нет такого Id)
        Subtask subtask2 = new Subtask(3, epicId, "TestSubtask1", "Description", TaskStatus.IN_PROGRESS);

        IOException ex1 = assertThrows(IOException.class, () -> manager.getSubtask(3));
        assertEquals(String.format("Сабтаска с ID: %s не существует", 3), ex1.getMessage());

        IOException ex2 = assertThrows(IOException.class, () -> manager.updateSubtask(subtask2));
        assertEquals("Невозможно обновить подзадачу: не указан ID || подзадача отсутствует.", ex2.getMessage());

        List<Task> subtaskAndEpicList1 = manager.getAllTasksList();
        assertEquals(2, subtaskAndEpicList1.size(), "Неверное количество Subtask & Epic.");
    }

    // d) Проверка изменения статуса и времени Epic при обновлении Subtask
    @Test
    public void shouldChangeEpicStatusWhenSubtaskUpdated() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask);

        assertEquals(TaskStatus.NEW, savedEpic.getStatus());

        Subtask subtask1 = new Subtask(2, epicId, "TestSubtask1", "Description", TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Не верный статус Epic");

        Subtask subtask2 = new Subtask(2, epicId, "TestSubtask2", "Description", TaskStatus.DONE);
        manager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, savedEpic.getStatus(), "Не верный статус Epic");
    }

    // e) Проверка времени Epic при добавлении Subtask
    @Test
    public void shouldChangeEpicTimeWhenSubtaskUpdated() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        final int subtaskId = manager.addSubtask(subtask);
        manager.getSubtask(subtaskId);

        assertNull(savedEpic.getStartTime(), "Время начала Epic установлено");
        assertNull(savedEpic.getDuration(), "Продолжительность Epic установлена");
        assertNull(savedEpic.getEndTime(), "Время окончания Epic установлено");

        Subtask testSubtask1 = new Subtask(2,
                epicId,
                "TestSubtask1",
                "Description",
                TaskStatus.IN_PROGRESS);
        testSubtask1.setStartTime(LocalDateTime.of(2022, 4, 16, 13, 45));
        testSubtask1.setDuration(Duration.ofDays(10));
        manager.updateSubtask(testSubtask1);

        assertEquals(LocalDateTime.of(2022, 4, 16, 13, 45),
                savedEpic.getStartTime(),
                "Epic's startTime was not changed");
        assertEquals(Duration.ofDays(10),
                savedEpic.getDuration(), "Epic's duration was not changed");
        assertEquals(LocalDateTime.of(2022, 4, 26, 13, 45),
                savedEpic.getEndTime(),
                "Epic endTime was not changed");
    }

    // 2.6.1. Удаление Task по идентификатору
    // a) Нормальные условия (Task существует)
    @Test
    public void shouldDeleteTask() throws IOException {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        final int taskId = manager.addTask(task);
        final Task savedTask = manager.getTask(taskId);

        assertNotNull(manager.getTask(taskId), "Task is not return");

        manager.deleteTask(taskId);
        assertFalse(manager.getAllTasksList().contains(savedTask));
        IOException ex = assertThrows(IOException.class, () -> manager.getTask(taskId));
        assertEquals(String.format("Задачи с ID: %s не существует", taskId), ex.getMessage());
    }

    // b) Исключительные условия (Task не существует)
    @Test
    public void shouldTrowIOExceptionIfDeleteTask() {
        Task task1 = new Task(1, "TestTask", "Description", TaskStatus.NEW);

        IOException ex = assertThrows(IOException.class, () -> manager.deleteTask(1));
        assertEquals(String.format("Задачи с ID: %s не существует", 1), ex.getMessage());

        assertFalse(manager.getAllTasksList().contains(task1), "TaskList contains Task");

        IOException ex1 = assertThrows(IOException.class, () -> manager.getTask(1));
        assertEquals(String.format("Задачи с ID: %s не существует", 1), ex1.getMessage());
    }

    // 2.6.2. Удаление Epic по идентификатору
    // a) Нормальные условия (Epic существует)
    @Test
    public void shouldDeleteEpic() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);
        assertNotNull(manager.getEpic(epicId), "Epic is not return");

        manager.deleteEpic(epicId);
        assertFalse(manager.getAllTasksList().contains(savedEpic), "Перечень содержит Epic");

        IOException ex = assertThrows(IOException.class, () -> manager.getEpic(epicId));
        assertEquals(String.format("Эпика с ID: %s не существует", epicId), ex.getMessage());
    }

    // b) Исключительные условия (Epic не существует)
    @Test
    public void shouldTrowIOExceptionIfDeleteEpic() {
        Epic epic = new Epic(1, "TestEpic", "Description", null);

        IOException ex = assertThrows(IOException.class, () -> manager.deleteEpic(1));
        assertEquals("Эпика с ID: 1 не существует", ex.getMessage());

        assertFalse(manager.getAllTasksList().contains(epic), "TaskList contains Epic");

        IOException ex1 = assertThrows(IOException.class, () -> manager.getEpic(1));
        assertEquals(String.format("Эпика с ID: %s не существует", 1), ex1.getMessage());
    }

    // 2.6.3. Удаление Subtask по идентификатору
    // a) Без проверок на изменения статуса и времени Epic
    @Test
    public void shouldDeleteSubtask() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        final int subtaskId = manager.addSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        assertNotNull(manager.getSubtask(subtaskId), "Subtask is not return");

        manager.deleteSubtask(subtaskId);
        assertFalse(manager.getAllTasksList().contains(savedSubtask), "Перечень содержит Subtask");
        IOException ex = assertThrows(IOException.class, () -> manager.getSubtask(subtaskId));
        assertEquals(String.format("Сабтаска с ID: %s не существует", subtaskId), ex.getMessage());
    }

    // b) Проверка изменения статуса Epic при удалении Subtask
    @Test
    public void shouldChangeEpicStatusWhenSubtaskDeleted() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        int subtaskId = manager.addSubtask(subtask);

        Subtask subtask1 = new Subtask(3, epicId, "TestSubtask1", "Description", TaskStatus.IN_PROGRESS);
        int subtaskId1 = manager.addSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Не верный статус Epic");

        manager.deleteSubtask(subtaskId1);
        assertEquals(TaskStatus.DONE, savedEpic.getStatus(), "Не верный статус Epic");

        manager.deleteSubtask(subtaskId);
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Не верный статус Epic");
    }

    // c) Проверка изменения времени Epic при удалении Subtask
    @Test
    public void shouldChangeEpicTimeWhenSubtaskRemove() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        subtask.setStartTime(LocalDateTime.of(2022, 4, 16, 13, 45));
        subtask.setDuration(Duration.ofDays(10));

        final int subtaskId = manager.addSubtask(subtask);
        manager.getSubtask(subtaskId);

        assertEquals(LocalDateTime.of(2022, 4, 16, 13, 45),
                savedEpic.getStartTime(),
                "Epic's startTime was not changed");
        assertEquals(Duration.ofDays(10),
                savedEpic.getDuration(), "Epic's duration was not changed");
        assertEquals(LocalDateTime.of(2022, 4, 26, 13, 45),
                savedEpic.getEndTime(),
                "Epic endTime was not changed");

        manager.deleteSubtask(subtaskId);

        assertNull(savedEpic.getStartTime(), "Время начала Epic установлено");
        assertNull(savedEpic.getDuration(), "Продолжительность Epic установлена");
        assertNull(savedEpic.getEndTime(), "Время окончания Epic установлено");
    }

    // d) Исключительные условия (Subtask не существует)
    @Test
    public void shouldTrowIOExceptionIfDeleteSubtask() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        manager.addEpic(epic);

        Subtask subtask = new Subtask(2, 1, "TestSubtask", "Description", TaskStatus.DONE);

        IOException ex = assertThrows(IOException.class, () -> manager.deleteSubtask(1));
        assertEquals("Подзадачи с ID: 1 не существует", ex.getMessage());

        assertFalse(manager.getAllTasksList().contains(subtask), "TaskList contains Subtask");

        IOException ex1 = assertThrows(IOException.class, () -> manager.getSubtask(1));
        assertEquals(String.format("Сабтаска с ID: %s не существует", 1), ex1.getMessage());
    }

    // 3.1. Получение списка подзадач для эпика.
    @Test
    public void shouldReturnEpicsList() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        int subtaskId = manager.addSubtask(subtask);
        Subtask savedSubtask = manager.getSubtask(subtaskId);

        Subtask subtask1 = new Subtask(3, epicId, "TestSubtask1", "Description", TaskStatus.NEW);
        int subtaskId1 = manager.addSubtask(subtask1);
        Subtask savedSubtask1 = manager.getSubtask(subtaskId1);

        List<Task> subtaskList = manager.getAllTasksList();
        assertFalse(subtaskList.isEmpty(), "SubtaskList is empty");
        assertEquals(3, subtaskList.size(), " Incorrect task qty. in SubtaskList");
        assertTrue(subtaskList.contains(savedSubtask), "SubtaskList doesn't contain Subtask");
        assertTrue(subtaskList.contains(savedSubtask1), "SubtaskList doesn't contain Subtask1");
    }

    @Test
    public void shouldGenerateNewId() {
        int id1 = manager.generateNewId();
        int id2 = manager.generateNewId();
        int id3 = manager.generateNewId();
        assertEquals(1, id1, "Incorrect ID");
        assertEquals(2, id2, "Incorrect ID");
        assertEquals(3, id3, "Incorrect ID");
    }

    @Test
    public void shouldReturnHistory() throws IOException {
        Epic epic = new Epic(1, "TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        Subtask subtask = new Subtask(2, epicId, "TestSubtask", "Description", TaskStatus.DONE);
        int subtaskId = manager.addSubtask(subtask);
        Subtask savedSubtask = manager.getSubtask(subtaskId);

        Subtask subtask1 = new Subtask(3, epicId, "TestSubtask1", "Description", TaskStatus.NEW);
        int subtaskId1 = manager.addSubtask(subtask1);
        Subtask savedSubtask1 = manager.getSubtask(subtaskId1);

        Task task = new Task(4, "TestTask", "Description", TaskStatus.NEW);
        final int taskId = manager.addTask(task);
        final Task savedTask = manager.getTask(taskId);

        List<Task> historyList = manager.history();
        assertFalse(historyList.isEmpty(), "HistoryList is empty");
        assertEquals(4, historyList.size(), " HistoryList qty. in HistoryList");
        assertTrue(historyList.contains(savedEpic), "HistoryList doesn't contain Epic");
        assertTrue(historyList.contains(savedSubtask), "HistoryList doesn't contain Subtask");
        assertTrue(historyList.contains(savedSubtask1), "HistoryList doesn't contain Subtask1");
        assertTrue(historyList.contains(savedTask), "HistoryList doesn't contain Task");
    }

    @Test
    public void shouldReturnTrueIfTasksCrossingByTime() throws IOException {
        Task task1 = new Task("TestTask", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2022, 4, 11, 13, 45));
        task1.setDuration(Duration.ofDays(10));
        manager.addTask(task1);

        Task task2 = new Task("TestTask", "Description", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2022, 4, 5, 13, 45));
        task2.setDuration(Duration.ofDays(10));
        manager.addTask(task2);

        Epic epic = new Epic("TestEpic", "Description", null);
        final int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "TestSubtask", "Description", TaskStatus.NEW);
        subtask.setStartTime(LocalDateTime.of(2022, 4, 1, 13, 45));
        subtask.setDuration(Duration.ofDays(2));
        manager.addSubtask(subtask);

        assertTrue(manager.isTasksCrossing(LocalDateTime.of(2022, 4, 12, 13, 45)),
                "returns false");
        assertFalse((manager.isTasksCrossing(LocalDateTime.of(2022, 4, 28, 13, 45))),
                "returns true");
    }
}

