package utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import management.history.HistoryManager;
import management.history.InMemoryHistoryManager;
import management.task.HttpTaskManager;
import management.task.InMemoryTaskManager;
import management.task.TaskManager;
import utilities.serializators.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Managers {

    public static TaskManager getDefault1() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getDefault() {
        String url = "http://localhost:8078";
        return new HttpTaskManager(getDefaultHistory(), url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
                .registerTypeAdapter(Epic.class, new EpicDeserializer())
                .create();
    }
}

