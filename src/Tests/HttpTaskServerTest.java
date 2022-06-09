package Tests;

import com.google.gson.Gson;
import http.HttpTaskServer;
import http.KVServer;
import management.task.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilities.TaskStatus;
import utilities.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    TaskManager manager;
    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    HttpClient client = HttpClient.newHttpClient();
    String url = "http://localhost:8080/tasks";
    Gson gson = Managers.getGson();

    @BeforeEach
    public void serversStart() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            manager = Managers.getDefault();
            httpTaskServer = new HttpTaskServer(manager);
            httpTaskServer.start();
        } catch (IOException exception) {
            exception.getMessage();
        }
    }

    @AfterEach
    public void serversStop() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    // Endpoint: http://localhost:8080/tasks/task (PUT, AddTask (No ID).
    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);

        var taskToGson = gson.toJson(task);
        final var body = HttpRequest.BodyPublishers.ofString(taskToGson);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Task is added with ID: %s", 1), response.body(), "Incorrect responseBody");

        assertFalse(manager.getAllTasksList().isEmpty(), "taskList is empty");

    }

    // Endpoint: http://localhost:8080/tasks/task (PUT, UpdateTask).
    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        var id = manager.addTask(task);

        Task taskUpd = new Task(id, "NewTitle", "NewDescription", TaskStatus.NEW);

        var taskToGson = gson.toJson(taskUpd);
        final var body = HttpRequest.BodyPublishers.ofString(taskToGson);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Task with ID: %s is updated", id), response.body(), "Incorrect responseBody");

        assertEquals(taskUpd, manager.getTask(id));
    }

    // Endpoint: http://localhost:8080/tasks/subtask (PUT, AddSubtask (No ID).
    @Test
    public void shouldAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);

        var subtaskToGson = gson.toJson(subtask);
        final var body = HttpRequest.BodyPublishers.ofString(subtaskToGson);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask"))
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Subtask is added with ID: %s", 2), response.body(), "Incorrect responseBody");

        assertFalse(manager.getAllTasksList().isEmpty(), "TaskList is empty");
        assertEquals(2, manager.getAllTasksList().size(), "Incorrect size of the TaskList");
    }

    // Endpoint: http://localhost:8080/tasks/subtask (PUT, UpdateSubtask).
    @Test
    public void shouldUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        var subtaskId = manager.addSubtask(subtask);

        Subtask subtaskUpd = new Subtask(subtaskId,
                epicId,
                "NewTitle",
                "NewDescription",
                TaskStatus.NEW);

        var subtaskToGson = gson.toJson(subtaskUpd);
        final var body = HttpRequest.BodyPublishers.ofString(subtaskToGson);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask"))
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Subtask with ID: %s is updated", subtaskId),
                response.body(),
                "Incorrect responseBody");

        assertEquals(subtaskUpd, manager.getSubtask(subtaskId));
    }

    // Endpoint: http://localhost:8080/tasks/subtask (PUT, AddEpic (No ID).
    @Test
    public void shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description", null);

        var epicToGson = gson.toJson(epic);
        final var body = HttpRequest.BodyPublishers.ofString(epicToGson);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic"))
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Epic is added with ID: %s", 1), response.body(), "Incorrect responseBody");

        assertFalse(manager.getAllTasksList().isEmpty(), "TaskList is empty");
        assertEquals(1, manager.getAllTasksList().size(), "Incorrect size of the TaskList");
    }

    // Endpoint: http://localhost:8080/tasks/subtask (PUT, UpdateEpic).
    @Test
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Epic epicUpd = new Epic(epicId, "NewTitle", "NewDescription", TaskStatus.NEW);

        var subtaskToGson = gson.toJson(epicUpd);
        final var body = HttpRequest.BodyPublishers.ofString(subtaskToGson);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic"))
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Epic with ID: %s is updated", epicId),
                response.body(),
                "Incorrect responseBody");

        assertEquals(epicUpd, manager.getEpic(epicId));
    }

    // case "GET"
    // (1) getTask (http://localhost:8080/tasks/task/?id=x)
    // (a) Task существует
    @Test
    public void shouldReturnTask() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        var taskId = manager.addTask(task);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task" + "/?id=" + taskId))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");

        assertEquals(gson.toJson(task), response.body(), "Incorrect ResponseBody");
        assertEquals(task, gson.fromJson(response.body(), Task.class), "Incorrect ResponseBody");
    }

    // (b) Task не существует
    @Test
    public void shouldNotReturnAbsentTask() throws IOException, InterruptedException {
        var random = new Random();
        final var randomId = random.nextInt(100);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task" + "/?id=" + randomId))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(response.statusCode(), 404, "Incorrect statusCode");
        assertEquals(String.format("Задачи с ID: %s не существует", randomId),
                response.body(),
                "Incorrect ResponseBody");
    }

    // (2) getEpic (http://localhost:8080/tasks/epic/?id=x)
    // (a) Epic существует
    @Test
    public void shouldReturnEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic" + "/?id=" + epicId))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");

        assertEquals(gson.toJson(epic), response.body(), "Incorrect ResponseBody");
        assertEquals(epic, gson.fromJson(response.body(), Epic.class), "Incorrect ResponseBody");
    }

    // (b) Epic не существует
    @Test
    public void shouldNotReturnAbsentEpic() throws IOException, InterruptedException {
        var random = new Random();
        final var randomId = random.nextInt(100);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic" + "/?id=" + randomId))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(response.statusCode(), 404, "Incorrect statusCode");
        assertEquals(String.format("Эпика с ID: %s не существует", randomId),
                response.body(),
                "Incorrect ResponseBody");
    }

    // (3) getSubtask (http://localhost:8080/tasks/subtask/?id=x)
    // (a) Subtask существует
    @Test
    public void shouldReturnSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        var subtaskId = manager.addSubtask(subtask);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask" + "/?id=" + subtaskId))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");

        assertEquals(gson.toJson(subtask), response.body(), "Incorrect ResponseBody");
        assertEquals(subtask, gson.fromJson(response.body(), Subtask.class), "Incorrect ResponseBody");
    }

    // // (b) Subtask не существует
    @Test
    public void shouldNotReturnAbsentSubtask() throws IOException, InterruptedException {
        var random = new Random();
        final var randomId = random.nextInt(100);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask" + "/?id=" + randomId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Сабтаска с ID: %s не существует", randomId),
                response.body(),
                "Incorrect ResponseBody");
    }

    // (4) GetEpicsSubtasks (http://localhost:8080/tasks/subtask/epic/?id=x)
    // (a) Epic существует
    @Test
    public void shouldReturnEpicsSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "TestSubtask", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask);

        Subtask subtask1 = new Subtask(epicId, "TestSubtask1", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask1);

        var subtasksList = manager.getEpicsSubtasks(epicId);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask" + "/epic" + "/?id=" + epicId))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");
        assertEquals(gson.toJson(subtasksList), response.body(), "Lists in gson format are not equal");
    }

    // (b) Epic не существует
    @Test
    public void shouldNotReturnEpicsSubtasks() throws IOException, InterruptedException {
        var random = new Random();
        final var randomId = random.nextInt(100);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask" + "/epic" + "/?id=" + randomId))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Невозможно получить перечень подзадач, " +
                        "т.к. эпик с ID: %s не существует.", randomId),
                response.body(),
                "Incorrect ResponseBody");
    }

    // (5) GetPrioritizedTasks (http://localhost:8080/tasks)
    // (a) Tasks set is not empty
    @Test
    public void shouldReturnPrioritizedTasksSet() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "Description", TaskStatus.NEW);
        manager.addTask(task);

        Task task1 = new Task("TestTask1", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2022, 4, 11, 13, 45));
        task1.setDuration(Duration.ofHours(10));
        manager.addTask(task1);

        Task task2 = new Task("TestTask2", "Description", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2022, 4, 2, 13, 45));
        task2.setDuration(Duration.ofHours(10));
        manager.addTask(task2);

        Epic epic = new Epic("TestEpic", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "TestSubtask", "Description", TaskStatus.NEW);
        subtask.setStartTime(LocalDateTime.of(2022, 4, 1, 13, 45));
        subtask.setDuration(Duration.ofHours(10));
        manager.addSubtask(subtask);

        Subtask subtask1 = new Subtask(epicId, "TestSubtask1", "Description", TaskStatus.NEW);
        subtask1.setStartTime(LocalDateTime.of(2022, 4, 3, 13, 45));
        subtask1.setDuration(Duration.ofHours(10));
        manager.addSubtask(subtask1);

        var taskSet = manager.getPrioritizedTasks();

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Incorrect statusCode");
        assertEquals(gson.toJson(taskSet), response.body(), "Sets in gson format are not equal");
    }

    // (b) Tasks set is empty
    @Test
    public void shouldReturnEmptyPrioritizedTasksSet() throws IOException, InterruptedException {
        // Проверим пустой ли taskSet (запрос через manager)
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "TasksSet is not empty");

        // Проверим пустой ли taskList (запрос через http)
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");
        assertTrue(response.body().isEmpty(), "Response body is not empty");
    }

    // (6) GetAllTasksList (http://localhost:8080/tasks/task)
    // (a) Task list is not empty
    @Test
    public void shouldReturnAllTaskList() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        manager.addTask(task);

        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask);

        var list = manager.getAllTasksList();

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");
        assertEquals(gson.toJson(list), response.body(), "Lists in gson format are not equal");
    }

    // (b) Task list is empty
    @Test
    public void shouldReturnEmptyTaskList() throws IOException, InterruptedException {
        // Проверим пустой ли taskList (запрос через manager)
        var list = manager.getAllTasksList();

        assertTrue(list.isEmpty(), "Task list is not empty");

        // Проверим пустой ли taskList (запрос через http)
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");
        assertTrue(response.body().isEmpty(), "Response body is not empty");
    }

    // (6) GetHistory (http://localhost:8080/tasks/history)
    // (a) HistoryList is not empty
    @Test
    public void shouldReturnHistory() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        var taskId = manager.addTask(task);

        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        var subtaskId = manager.addSubtask(subtask);

        // Проверим функцию заполнения history посредством запросов
        var requestTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task" + "/?id=" + taskId))
                .GET()
                .build();
        client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        var requestEpic = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic" + "/?id=" + epicId))
                .GET()
                .build();
        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        var requestSubtask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask" + "/?id=" + subtaskId))
                .GET()
                .build();
        client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        // Проверим наличие данных по истории в manager
        final var historyList = manager.history();

        assertFalse(historyList.isEmpty(), "HistoryList is empty");
        assertEquals(3, historyList.size(), "Size of historyList is incorrect");

        // Проверим, возвращает ли http данные по истории
        var requestHistory = HttpRequest.newBuilder()
                .uri(URI.create(url + "/history"))
                .GET()
                .build();
        var response = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");
        assertEquals(gson.toJson(manager.history()), response.body(), "Lists in gson format are not equal");
    }

    // (b) HistoryList is empty
    @Test
    public void shouldReturnEmptyHistory() throws IOException, InterruptedException {
        // Проверим наличие данных по истории в manager
        final var historyList = manager.history();

        assertTrue(historyList.isEmpty(), "HistoryList is empty");

        // Проверим, возвращает ли http данные по истории
        var requestHistory = HttpRequest.newBuilder()
                .uri(URI.create(url + "/history"))
                .GET()
                .build();
        var response = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Incorrect statusCode");
        assertTrue(response.body().isEmpty(), "Response body is not empty");
    }

    // case "DELETE"
    // (1) deleteTask (http://localhost:8080/tasks/task/?id=x)
    // (a) Task существует (нет других tasks, epics, subtasks)
    @Test
    public void shouldDeleteTaskIfNoOtherTasksExist() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        var taskId = manager.addTask(task);

        // Проверим наличие task в manager
        final var taskListBeforeDeleting = manager.getAllTasksList();

        assertFalse(taskListBeforeDeleting.isEmpty(), "TasksList is empty");
        assertEquals(1, taskListBeforeDeleting.size(), "Size of TasksList is incorrect");

        // Запрос на удаление task
        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task" + "/?id=" + taskId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Task with ID: %s has been deleted",
                taskId), response.body(), "Incorrect responseBody");


        // Проверим факт удаления (запрос через Manager)
        final var taskListAfterDeleting = manager.getAllTasksList();

        assertTrue(taskListAfterDeleting.isEmpty(), "TasksList is empty");

        // Проверим факт удаления (запрос через http)
        var requestTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();
        var response1 = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Incorrect statusCode");
        assertTrue(response1.body().isEmpty(), "Response body is not empty");
    }

    // (b) Task существует (есть другие tasks, epics, subtasks)
    @Test
    public void shouldDeleteTaskIfOtherTasksExist() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        var taskId = manager.addTask(task);

        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        manager.addTask(task1);

        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask);

        // Проверим наличие task в manager
        final var taskListBeforeDeleting = manager.getAllTasksList();

        assertFalse(taskListBeforeDeleting.isEmpty(), "TasksList is empty");
        assertEquals(4, taskListBeforeDeleting.size(), "Size of TasksList is incorrect");

        // Запрос на удаление task
        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task" + "/?id=" + taskId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Task with ID: %s has been deleted",
                taskId), response.body(), "Incorrect responseBody");

        // Проверим факт удаления (запрос через Manager)
        final var taskListAfterDeleting = manager.getAllTasksList();

        assertEquals(3, taskListAfterDeleting.size(), "Size of TasksList is incorrect");

        // Проверим факт удаления (запрос через http)
        var requestTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();
        var response1 = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Incorrect statusCode");
        assertEquals(gson.toJson(taskListAfterDeleting), response1.body(), "Lists in gson format are not equal");
    }

    // (c) Task не существует (не существующий Id)
    @Test
    public void shouldNotDeleteTask() throws IOException, InterruptedException {
        var random = new Random();
        final var randomId = random.nextInt(100);

        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task" + "/?id=" + randomId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Задачи с ID: %s не существует", randomId),
                response.body(),
                "Incorrect responseBody");
    }

    // (2) deleteEpic (http://localhost:8080/tasks/epic/?id=x)
    // (a) Epic существует (нет других tasks, epics, subtasks)
    @Test
    public void shouldDeleteEpicIfNoOtherTasksExist() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        // Проверим наличие task в manager
        final var taskListBeforeDeleting = manager.getAllTasksList();

        assertFalse(taskListBeforeDeleting.isEmpty(), "TasksList is empty");
        assertEquals(1, taskListBeforeDeleting.size(), "Size of TasksList is incorrect");

        // Запрос на удаление
        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic" + "/?id=" + epicId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Epic with ID: %s has been deleted",
                epicId), response.body(), "Incorrect responseBody");

        // Проверим факт удаления (запрос через Manager)
        final var taskListAfterDeleting = manager.getAllTasksList();

        assertTrue(taskListAfterDeleting.isEmpty(), "TasksList is empty");

        // Проверим факт удаления (запрос через http)
        var requestTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();
        var response1 = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Incorrect statusCode");
        assertTrue(response1.body().isEmpty(), "Response body is not empty");
    }

    // (b) Epic существует (есть другие tasks, epics, subtasks)
    @Test
    public void shouldDeleteEpicIfOtherTasksExist() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        manager.addTask(task);

        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        manager.addTask(task1);

        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask);

        // Проверим наличие task в manager
        final var taskListBeforeDeleting = manager.getAllTasksList();

        assertFalse(taskListBeforeDeleting.isEmpty(), "TasksList is empty");
        assertEquals(4, taskListBeforeDeleting.size(), "Size of TasksList is incorrect");

        // Запрос на удаление
        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic" + "/?id=" + epicId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Epic with ID: %s has been deleted",
                epicId), response.body(), "Incorrect responseBody");

        // Проверим факт удаления (запрос через Manager)
        final var taskListAfterDeleting = manager.getAllTasksList();

        assertEquals(2, taskListAfterDeleting.size(), "Size of TasksList is incorrect");

        // Проверим факт удаления (запрос через http)
        var requestTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();
        var response1 = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Incorrect statusCode");
        assertEquals(gson.toJson(taskListAfterDeleting), response1.body(), "Lists in gson format are not equal");
    }

    // (c) Epic не существует (не существующий Id)
    @Test
    public void shouldNotDeleteEpic() throws IOException, InterruptedException {
        var random = new Random();
        final var randomId = random.nextInt(100);

        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic" + "/?id=" + randomId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Эпика с ID: %s не существует", randomId),
                response.body(),
                "Incorrect responseBody");
    }

    // (3) deleteSubtask (http://localhost:8080/tasks/subtask/?id=x)
    // (a) Subtask существует (нет других tasks, epics, subtasks)
    @Test
    public void shouldDeleteSubtaskIfNoOtherTasksExist() throws IOException, InterruptedException {
        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        var subtaskId = manager.addSubtask(subtask);

        // Проверим наличие task в manager
        final var taskListBeforeDeleting = manager.getAllTasksList();

        assertFalse(taskListBeforeDeleting.isEmpty(), "TasksList is empty");
        assertEquals(2, taskListBeforeDeleting.size(), "Size of TasksList is incorrect");

        // Запрос на удаление
        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask" + "/?id=" + subtaskId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Subtask with ID: %s has been deleted",
                subtaskId), response.body(), "Incorrect responseBody");

        // Проверим факт удаления (запрос через Manager)
        final var taskListAfterDeleting = manager.getAllTasksList();

        assertEquals(1, taskListAfterDeleting.size(), "Size of the task list is incorrect");

        // Проверим факт удаления (запрос через http)
        var requestTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();
        var response1 = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Incorrect statusCode");
        assertEquals(gson.toJson(taskListAfterDeleting), response1.body(), "Lists in gson format are not equal");
    }

    // (b) Subtask существует (есть другие tasks, epics, subtasks)
    @Test
    public void shouldDeleteSubtaskIfOtherTasksExist() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        manager.addTask(task);

        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        manager.addTask(task1);

        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        var subtaskId = manager.addSubtask(subtask);

        // Проверим наличие task в manager
        final var taskListBeforeDeleting = manager.getAllTasksList();

        assertFalse(taskListBeforeDeleting.isEmpty(), "TasksList is empty");
        assertEquals(4, taskListBeforeDeleting.size(), "Size of TasksList is incorrect");

        // Запрос на удаление
        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask" + "/?id=" + subtaskId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Subtask with ID: %s has been deleted",
                subtaskId), response.body(), "Incorrect responseBody");

        // Проверим факт удаления (запрос через Manager)
        final var taskListAfterDeleting = manager.getAllTasksList();

        assertEquals(3, taskListAfterDeleting.size(), "Size of TasksList is incorrect");

        // Проверим факт удаления (запрос через http)
        var requestTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();
        var response1 = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Incorrect statusCode");
        assertEquals(gson.toJson(taskListAfterDeleting), response1.body(), "Lists in gson format are not equal");
    }

    // (c) Subtask не существует (не существующий Id)
    @Test
    public void shouldNotDeleteSubtask() throws IOException, InterruptedException {
        var random = new Random();
        final var randomId = random.nextInt(100);

        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask" + "/?id=" + randomId))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Incorrect statusCode");
        assertEquals(String.format("Подзадачи с ID: %s не существует", randomId),
                response.body(),
                "Incorrect responseBody");
    }

    // (4) clearTasksList (http://localhost:8080/tasks/task)
    @Test
    public void shouldDeleteAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        manager.addTask(task);

        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        manager.addTask(task1);

        Epic epic = new Epic("Title", "Description", null);
        var epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(epicId, "Title", "Description", TaskStatus.NEW);
        manager.addSubtask(subtask);

        // Проверим наличие task в manager
        final var taskListBeforeDeleting = manager.getAllTasksList();

        assertFalse(taskListBeforeDeleting.isEmpty(), "TasksList is empty");
        assertEquals(4, taskListBeforeDeleting.size(), "Size of TasksList is incorrect");

        // Удаляем все задачи
        var requestForDel = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .DELETE()
                .build();
        var response = client.send(requestForDel, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode(), "Incorrect statusCode");
        assertEquals("All tasks, epics and subtasks have been deleted",
                response.body(),
                "Incorrect responseBody");

        // Проверим факт удаления (запрос через Manager)
        final var taskListAfterDeleting = manager.getAllTasksList();

        assertTrue(taskListAfterDeleting.isEmpty(), "TasksList is not empty");

        // Проверим факт удаления (запрос через http)
        var requestTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .GET()
                .build();
        var response1 = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode(), "Incorrect statusCode");
        assertTrue(response1.body().isEmpty(), "TasksList is not empty");
    }
}
