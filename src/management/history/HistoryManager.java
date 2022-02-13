package management.history;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void addToHistory(Task task);

    void deleteFromHistory(int taskId);

    List<Task> getHistory();
}
