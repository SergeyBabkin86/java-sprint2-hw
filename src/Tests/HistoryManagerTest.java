package Tests;

import management.history.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilities.TaskStatus;
import utilities.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {

    public HistoryManager historyManager;

    @BeforeEach
    public void createNewHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    // 1. Добавление элемента в историю
    @Test
    public void shouldLinkTaskLast() {
        // а) Нет других элементов
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> returnedHistoryList = historyManager.getHistory();
        assertEquals(1, returnedHistoryList.size(), "Количество задач не совпадает");
        assertEquals(0, returnedHistoryList.indexOf(task), "Задача не на последнем месте списка");

        // b) Есть другие элементы
        Epic epic = new Epic(2, "TestEpic", "Description", null);
        historyManager.add(epic);

        List<Task> returnedHistoryList1 = historyManager.getHistory();
        assertEquals(2, returnedHistoryList1.size(), "Количество задач не совпадает");
        assertEquals(1, returnedHistoryList1.indexOf(epic), "Задача не на последнем месте списка");

        Subtask subtask = new Subtask(3, 2, "TestSubtask", "Description", TaskStatus.NEW);
        historyManager.add(subtask);

        List<Task> returnedHistoryList2 = historyManager.getHistory();
        assertEquals(3, returnedHistoryList2.size(), "Количество задач не совпадает");
        assertEquals(2, returnedHistoryList2.indexOf(subtask), "Задача на последнем месте списка");
    }

    // 2. Удаление элемента
    // а) Удаление первого элемента
    @Test
    public void shouldRemoveFirstElement() {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        historyManager.add(task);

        Epic epic = new Epic(2, "TestEpic", "Description", null);
        historyManager.add(epic);

        Subtask subtask = new Subtask(3, 2, "TestSubtask", "Description", TaskStatus.NEW);
        historyManager.add(subtask);

        List<Task> returnedHistoryList = historyManager.getHistory();
        assertEquals(3, returnedHistoryList.size(), "Количество задач не совпадает");

        historyManager.remove(1);

        List<Task> returnedHistoryList1 = historyManager.getHistory();
        assertEquals(2, returnedHistoryList1.size(), "Количество задач не совпадает");
        assertEquals(epic, returnedHistoryList1.get(0), "Не верный порядок истории после удаления");
        assertEquals(subtask, returnedHistoryList1.get(1), "Не верный порядок истории после удаления");
    }

    // b) Удаление элемента из середины
    @Test
    public void shouldRemoveMiddleElement() {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        historyManager.add(task);

        Epic epic = new Epic(2, "TestEpic", "Description", null);
        historyManager.add(epic);

        Subtask subtask = new Subtask(3, 2, "TestSubtask", "Description", TaskStatus.NEW);
        historyManager.add(subtask);

        List<Task> returnedHistoryList = historyManager.getHistory();
        assertEquals(3, returnedHistoryList.size(), "Количество задач не совпадает");

        historyManager.remove(2);

        List<Task> returnedHistoryList1 = historyManager.getHistory();
        assertEquals(2, returnedHistoryList1.size(), "Количество задач не совпадает");
        assertEquals(task, returnedHistoryList1.get(0), "Не верный порядок истории после удаления");
        assertEquals(subtask, returnedHistoryList1.get(1), "Не верный порядок истории после удаления");
    }

    // с) Удаление последнего элемента
    @Test
    public void shouldRemoveLastElement() {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        historyManager.add(task);

        Epic epic = new Epic(2, "TestEpic", "Description", null);
        historyManager.add(epic);

        Subtask subtask = new Subtask(3, 2, "TestSubtask", "Description", TaskStatus.NEW);
        historyManager.add(subtask);

        List<Task> returnedHistoryList = historyManager.getHistory();
        assertEquals(3, returnedHistoryList.size(), "Количество задач не совпадает");

        historyManager.remove(3);

        List<Task> returnedHistoryList1 = historyManager.getHistory();
        assertEquals(2, returnedHistoryList1.size(), "Количество задач не совпадает");
        assertEquals(task, returnedHistoryList1.get(0), "Не верный порядок истории после удаления");
        assertEquals(epic, returnedHistoryList1.get(1), "Не верный порядок истории после удаления");
    }

    // 3. Получение списка истории
    // а) Пустая история
    @Test
    public void shouldReturnEmptyHistory() {
        List<Task> returnedHistoryList = historyManager.getHistory();
        assertTrue(returnedHistoryList.isEmpty(), "Количество задач больше нуля");
    }

    // b) Не пустая история
    @Test
    public void shouldReturnHistory() {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        historyManager.add(task);

        Epic epic = new Epic(2, "TestEpic", "Description", null);
        historyManager.add(epic);

        Subtask subtask = new Subtask(3, 2, "TestSubtask", "Description", TaskStatus.NEW);
        historyManager.add(subtask);

        List<Task> returnedHistoryList = historyManager.getHistory();
        assertEquals(3, returnedHistoryList.size(), "Количество задач не совпадает");
        assertTrue(returnedHistoryList.contains(task), "Список не содержит задач");
        assertTrue(returnedHistoryList.contains(epic), "Список не содержит задач");
        assertTrue(returnedHistoryList.contains(subtask), "Список не содержит задач");
    }

    // с) Дублирование элементов
    @Test
    public void shouldReturnHistoryWithoutRepetitiveElements() {
        Task task = new Task(1, "TestTask", "Description", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task);

        Epic epic = new Epic(2, "TestEpic", "Description", null);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(epic);

        Subtask subtask = new Subtask(3, 2, "TestSubtask", "Description", TaskStatus.NEW);
        historyManager.add(subtask);
        historyManager.add(subtask);
        historyManager.add(subtask);

        List<Task> returnedHistoryList = historyManager.getHistory();
        assertEquals(3, returnedHistoryList.size(), "Количество задач не совпадает");
        assertEquals(task, returnedHistoryList.get(0), "Не верный порядок истории после удаления");
        assertEquals(epic, returnedHistoryList.get(1), "Не верный порядок истории после удаления");
        assertEquals(subtask, returnedHistoryList.get(2), "Не верный порядок истории после удаления");
    }
}
