package utilities;

import management.history.HistoryManager;
import management.history.InMemoryHistoryManager;
import management.task.InMemoryTaskManager;
import management.task.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
