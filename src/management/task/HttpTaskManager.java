package management.task;

import com.google.gson.Gson;
import http.KVTaskClient;
import management.history.HistoryManager;
import utilities.Managers;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = Managers.getGson();

    public HttpTaskManager(HistoryManager defaultHistory, String url) {
        super(defaultHistory, url);
        this.client = new KVTaskClient(url);
        client.register();
    }

    @Override
    public void save() {
        String savedTasks = gson.toJson(getAllTasksList());
        String savedHistory = gson.toJson(history());
        try {
            client.save("savedTasks", savedTasks);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            client.save("savedHistory", savedHistory);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
