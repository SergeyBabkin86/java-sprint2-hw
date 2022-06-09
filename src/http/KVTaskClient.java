package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String REQEST_TEMPLATE = "%s/%s/%s?API_KEY=%s";
    private final String url;
    private String apiKey;

    public KVTaskClient(String url) {
        this.url = url;
    }

    private String registerUrl() {
        return String.format("%s/register", this.url);
    }

    private String saveUrl(String key) {
        if (apiKey == null) {
            throw new IllegalStateException("Call KVTaskClient register before using method save");
        }
        return String.format(REQEST_TEMPLATE, this.url, "save", key, apiKey);
    }

    private String loadUrl(String key) {
        if (apiKey == null) {
            throw new IllegalStateException("Call KVTaskClient.register before using method save");
        }
        return String.format(REQEST_TEMPLATE, this.url, "load", key, apiKey);
    }

    public void register() {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(registerUrl()))
                .GET()
                .build();
        try {
            var apiKey = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            if (apiKey == null) {
                throw new IllegalStateException("Can't register to kv storage");
            } else {
                this.apiKey = apiKey;
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Can't register to kv storage");
        }
    }

    public void save(String id, String gson) throws IOException, InterruptedException {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(saveUrl(id)))
                .POST(HttpRequest.BodyPublishers.ofString(gson))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("Task with id already exists");
        }
    }

    public String load(String key) {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(loadUrl(key)))
                .build();
        try {
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 400) {
                System.out.println("Ответ от сервера не соответствует ожидаемому.");
                return null;
            } else {
                return response.body();

            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}