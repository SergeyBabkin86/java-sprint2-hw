package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import utilities.serializators.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilities.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class KVServer {
    public static final int PORT = 8078;
    private final String API_KEY;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        API_KEY = generateApiKey();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", exchange -> {
            try {
                System.out.println("\n/register");
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        KVServer.this.sendText(exchange, API_KEY);
                        break;
                    default:
                        System.out.println("/register ждёт GET-запрос, а получил " + exchange.getRequestMethod());
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/save", h -> {
            try {
                System.out.println("\n/save");
                if (!KVServer.this.hasAuth(h)) {
                    System.out.println("Запрос не авторизован, нужен параметр в query API_KEY со значением апи-ключа");
                    h.sendResponseHeaders(403, 0);
                    return;
                }
                switch (h.getRequestMethod()) {
                    case "POST":
                        String key = h.getRequestURI().getPath().substring("/save/".length());
                        if (key.isEmpty()) {
                            System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                            h.sendResponseHeaders(400, 0);
                            return;
                        }
                        String value = KVServer.this.readText(h);
                        if (value.isEmpty()) {
                            System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                            h.sendResponseHeaders(400, 0);
                            return;
                        }
                        data.put(key, value);
                        System.out.println("Значение для ключа " + key + " успешно обновлено!");
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });
        server.createContext("/load", h -> {
            try {
                System.out.println("\n/load");
                if (!KVServer.this.hasAuth(h)) {
                    System.out.println("Запрос не авторизован, нужен параметр в query API_KEY со значением апи-ключа");
                    h.sendResponseHeaders(403, 0);
                    return;
                }
                switch (h.getRequestMethod()) {
                    case "GET":
                        String key = h.getRequestURI().getPath().substring("/load/".length());
                        if (key.isEmpty()) {
                            System.out.println("Key для сохранения пустой. key указывается в пути: /load/{key}");
                            h.sendResponseHeaders(400, 0);
                            return;
                        }
                        if (!data.containsKey(key)) {
                            System.out.printf("Data не содержит value c ключом: %s\n", key);
                            h.sendResponseHeaders(400, 0);
                            return;
                        }
                        KVServer.this.sendText(h, data.get(key));
                        System.out.println("Загружено значение для ключа " + key + ": " + data.get(key));
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/save ждёт GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });
    }

    public void start() {
        System.out.println("Запускаем KVServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_KEY: " + API_KEY);
        server.start();
    }

    public void stop() {
        System.out.println("KVServer остановлен");
        server.stop(0);
    }

    private String generateApiKey() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_KEY=" + API_KEY) || rawQuery.contains("API_KEY=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        //byte[] resp = jackson.writeValueAsBytes(obj);
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
                .registerTypeAdapter(Epic.class, new EpicDeserializer())
                .create();


        Task task1 = new Task(1, "Задача1", "Описание1", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Эпик2", "ЭпикОписание2", null);
        Subtask subtask4 = new Subtask(4, 2, "Подзадача4", "Описание4", TaskStatus.NEW);

        new KVServer().start();

        KVTaskClient client = new KVTaskClient("http://localhost:8078");

        client.register();
        client.save(task1.getId().toString(), gson.toJson(task1));
        client.save(epic2.getId().toString(), gson.toJson(epic2));
        client.save(subtask4.getId().toString(), gson.toJson(subtask4));


        String a = client.load("1");
        String b = client.load("4");
        String c = client.load("2");

        System.out.println("А вот то что загрузилось");
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);

    }


}
