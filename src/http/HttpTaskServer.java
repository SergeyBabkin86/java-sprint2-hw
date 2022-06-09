package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import management.task.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilities.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {

    private final TaskManager manager;
    private final HttpServer server;
    private final Gson gson = Managers.getGson();

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer(TaskManager httpManager) throws IOException {

        this.manager = httpManager;
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        server.createContext("/tasks", exchange -> {
            int responseCode = 0;
            String response = "";
            String path = exchange.getRequestURI().getPath();
            String[] spiltPath = path.split("/");
            String query = exchange.getRequestURI().getQuery();

            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (query != null) {
                        int id = Integer.parseInt(query.split("=")[1]);
                        if (spiltPath.length == 3) {
                            if ("task".equals(spiltPath[2])) {
                                try {
                                    Task task = manager.getTask(id);
                                    responseCode = 200;
                                    response = gson.toJson(task);
                                } catch (IOException e) {
                                    responseCode = 404;
                                    response = e.getMessage();
                                }
                            } else if ("epic".equals(spiltPath[2])) {
                                try {
                                    Epic epic = manager.getEpic(id);
                                    responseCode = 200;
                                    response = gson.toJson(epic);
                                } catch (IOException e) {
                                    responseCode = 404;
                                    response = e.getMessage();
                                }
                            } else if ("subtask".equals(spiltPath[2])) {
                                try {
                                    Subtask subtask = manager.getSubtask(id);
                                    responseCode = 200;
                                    response = gson.toJson(subtask);
                                } catch (IOException e) {
                                    responseCode = 404;
                                    response = e.getMessage();
                                }
                            } else {
                                responseCode = 400;
                                response = "Incorrect request. Check endpoint.";
                            }
                        } else if (spiltPath.length == 4) {
                            if ("subtask".equals(spiltPath[2]) && "epic".equals(spiltPath[3])) {
                                try {
                                    List<Subtask> subtaskList = manager.getEpicsSubtasks(id);
                                    responseCode = 200;
                                    response = gson.toJson(subtaskList);
                                } catch (IOException e) {
                                    responseCode = 404;
                                    response = e.getMessage();
                                }
                            }

                        } else {
                            responseCode = 400;
                            response = "Incorrect request. Check endpoint.";
                        }
                    } else {
                        if (path.endsWith("tasks") && spiltPath.length == 2) {
                            if (!manager.getPrioritizedTasks().isEmpty()) {
                                responseCode = 200;
                                response = gson.toJson(manager.getPrioritizedTasks());
                            } else {
                                responseCode = 200;
                                response = "";
                            }
                        } else if (path.endsWith("task") && spiltPath.length == 3) {
                            if (!manager.getAllTasksList().isEmpty()) {
                                responseCode = 200;
                                response = gson.toJson(manager.getAllTasksList());
                            } else {
                                responseCode = 200;
                                response = "";
                            }
                        } else if (path.endsWith("history") && spiltPath.length == 3) {
                            if (!manager.history().isEmpty()) {
                                responseCode = 200;
                                response = gson.toJson(manager.history());
                            } else {
                                responseCode = 200;
                                response = "";
                            }
                        } else {
                            responseCode = 400;
                            response = "Incorrect request. Check endpoint.";
                        }
                    }
                    break;
                case "POST":
                    var inputStream = exchange.getRequestBody();
                    var body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

                    if (spiltPath.length == 3) {
                        switch (spiltPath[2]) {
                            case "task":
                                Task task = gson.fromJson(body, Task.class);
                                if (task.getId() != null) {
                                    try {
                                        manager.updateTask(task);
                                        response = String.format("Task with ID: %s is updated", task.getId());
                                        responseCode = 201;
                                    } catch (IOException e) {
                                        response = e.getMessage();
                                        responseCode = 404;
                                    }
                                } else {
                                    try {
                                        Integer taskId = manager.addTask(task);
                                        response = "Task is added with ID: " + taskId;
                                        responseCode = 201;
                                    } catch (IOException e) {
                                        responseCode = 400;
                                        response = e.getMessage();
                                    }
                                }
                                break;
                            case "epic":
                                Epic epic = gson.fromJson(body, Epic.class);
                                if (epic.getId() != null) {
                                    try {
                                        manager.updateEpic(epic);
                                        response = String.format("Epic with ID: %s is updated", epic.getId());
                                        responseCode = 201;
                                    } catch (IOException e) {
                                        response = e.getMessage();
                                        responseCode = 404;
                                    }
                                } else {
                                    try {
                                        Integer epicId = manager.addEpic(epic);
                                        response = "Epic is added with ID: " + epicId;
                                        responseCode = 201;
                                    } catch (IOException e) {
                                        responseCode = 400;
                                        response = e.getMessage();
                                    }
                                }
                                break;
                            case "subtask":
                                Subtask subtask = gson.fromJson(body, Subtask.class);
                                if (subtask.getId() != null) {
                                    try {
                                        manager.updateSubtask(subtask);
                                        response = String.format("Subtask with ID: %s is updated", subtask.getId());
                                        responseCode = 201;
                                    } catch (IOException e) {
                                        response = e.getMessage();
                                        responseCode = 404;
                                    }
                                } else {
                                    try {
                                        Integer subtaskId = manager.addSubtask(subtask);
                                        response = "Subtask is added with ID: " + subtaskId;
                                        responseCode = 201;
                                    } catch (IOException e) {
                                        responseCode = 400;
                                        response = e.getMessage();
                                    }
                                }
                                break;
                        }
                    } else {
                        responseCode = 400;
                        response = "Incorrect request. Check endpoint!";
                    }
                    break;
                case "DELETE":
                    responseCode = 202;
                    if (query != null) {
                        int id = Integer.parseInt(query.split("=")[1]);
                        switch (spiltPath[2]) {
                            case "task":
                                try {
                                    manager.deleteTask(id);
                                    response = String.format("Task with ID: %s has been deleted", id);
                                } catch (IOException e) {
                                    response = e.getMessage();
                                    responseCode = 404;
                                }
                                break;
                            case "epic":
                                try {
                                    manager.deleteEpic(id);
                                    response = String.format("Epic with ID: %s has been deleted", id);
                                } catch (IOException e) {
                                    response = e.getMessage();
                                    responseCode = 404;
                                }
                                break;
                            case "subtask":
                                try {
                                    manager.deleteSubtask(id);
                                    response = String.format("Subtask with ID: %s has been deleted", id);
                                } catch (IOException e) {
                                    response = e.getMessage();
                                    responseCode = 404;
                                }
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + spiltPath[2]);
                        }
                    } else {
                        if (spiltPath.length == 3 && path.endsWith("task")) {
                            manager.clearTaskLists();
                            response = "All tasks, epics and subtasks have been deleted";
                        } else {
                            responseCode = 400;
                            response = "Check endpoint";
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + exchange.getRequestMethod());
            }

            exchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
    }

    public void start() {
        System.out.println("Запускаем httpTaskServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        this.server.start();
    }

    public void stop() {
        this.server.stop(0);
        System.out.println("httpTaskServer остановлен");
    }
}