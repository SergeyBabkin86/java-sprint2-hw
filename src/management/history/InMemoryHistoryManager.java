package management.history;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public List<Task> historyLog = new ArrayList<>(10);

    @Override
    public void addToHistory(Task task) {
        if (historyLog.size() == 10) {
            historyLog.remove(0);
        }
        historyLog.add(task);
    }

    @Override
    public void deleteFromHistory(int id) {
        historyLog.removeIf(task -> task.getId() == id);
    }

    @Override
    public List<Task> getHistory() {
        return historyLog;
    }
}
