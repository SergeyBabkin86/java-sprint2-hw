package management.task;

import management.history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id = 1;
    private final HistoryManager historyManager;

    Map<Integer, Task> taskList = new HashMap<>();
    Map<Integer, Subtask> subtaskList = new HashMap<>();
    Map<Integer, Epic> epicList = new HashMap<>();

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override // 2.1 Получение списка всех задач.
    public List<Object> getAllTasksList() {
        List<Object> allTasksList = new ArrayList<>();
        allTasksList.addAll(taskList.values());
        allTasksList.addAll(epicList.values());
        allTasksList.addAll(subtaskList.values());
        return allTasksList;
    }

    @Override // 2.2 Удаление всех задач.
    public void clearTaskLists() {
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
        System.out.println("Все списки задач очищены.");
    }

    @Override // 2.3.2 Получение задачи по идентификатору.
    public Task getTask(int id) {
        if (!taskList.containsKey(id)) {
            System.out.println("Задачи с ID: " + id + " не существует");
        }
        historyManager.addToHistory(taskList.get(id)); // Добавляем в историю
        return taskList.get(id);
    }

    @Override // 2.3.1 Получение эпика по идентификатору.
    public Epic getEpic(int id) {
        if (!epicList.containsKey(id)) {
            System.out.println("Эпика с ID: " + id + " не существует");
        }
        historyManager.addToHistory(epicList.get(id)); // Добавляем в историю
        return epicList.get(id);
    }

    @Override // 2.3.3 Получение сабтаска по идентификатору.
    public Subtask getSubtask(int id) {
        if (!subtaskList.containsKey(id)) {
            System.out.println("Сабтаска с ID: " + id + " не существует");
        }
        historyManager.addToHistory(subtaskList.get(id)); // Добавляем в историю
        return subtaskList.get(id);
    }

    @Override // 2.4.1. Создание таска
    public void addTask(Task task) {
        task.setId(generateNewId());
        taskList.put(task.getId(), task);
    }

    @Override // 2.4.2. Создание эпика
    public void addEpic(Epic epic) {
        epic.setId(generateNewId());
        epicList.put(epic.getId(), epic);
        epic.amendEpicStatus();
    }

    @Override // 2.4.3. Создание сабтаска
    public void addSubtask(Subtask subTask) {
        int epicId = subTask.getEpicId();
        subTask.setId(generateNewId());
        subtaskList.put(subTask.getId(), subTask);
        epicList.get(epicId).subtasksLinked.add(subTask);
        epicList.get(epicId).amendEpicStatus();
    }

    @Override // 2.5.1. Обновление таска
    public void updateTask(Task task) {
        Integer taskId = task.getId();
        taskList.replace(taskId, task);
    }

    @Override // 2.5.2. Обновление эпика
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        epicList.replace(epicId, epic);
        epic.subtasksLinked.addAll(getEpicsSubtasks(epicId));
        epic.amendEpicStatus();
    }

    @Override // 2.5.3. Обновление сабтаска
    public void updateSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        int indexInEpic = epicList.get(epicId).subtasksLinked.indexOf(subtask);
        epicList.get(epicId).subtasksLinked.set(indexInEpic, subtask);
        subtaskList.replace(subtask.getId(), subtask);
        epicList.get(epicId).amendEpicStatus();
    }

    @Override // 2.6.1. Удаление задачи по идентификатору
    public void deleteTask(int taskId) {
        if (taskList.containsKey(taskId)) {
            taskList.remove(taskId);
            historyManager.deleteFromHistory(taskId); // Удаляем таск из истории
        } else {
            System.out.println("Задачи с ID: " + taskId + " не существует");
        }
    }

    @Override // 2.6.2. Удаление эпика по идентификатору
    public void deleteEpic(int epicId) {
        if (epicList.containsKey(epicId)) {
            epicList.remove(epicId);
            historyManager.deleteFromHistory(epicId); // Удаляем эпик из истории
            for (Integer subtaskKey : subtaskList.keySet()) {
                if (subtaskList.get(subtaskKey).getEpicId() == epicId) {
                    subtaskList.remove(subtaskKey);
                    historyManager.deleteFromHistory(subtaskKey); // Удаляем из истории все сабтаски, привязанные к эпику
                }
            }
        } else {
            System.out.println("Эпика с ID: " + epicId + " не существует");
        }
    }

    @Override // 2.6.3. Удаление сабтаска по идентификатору
    public void deleteSubtask(int subtaskId) {
        if (subtaskList.containsKey(subtaskId)) {
            int epicId = subtaskList.get(subtaskId).getEpicId();
            epicList.get(epicId).subtasksLinked.remove(subtaskList.get(subtaskId));
            subtaskList.remove(subtaskId);
            historyManager.deleteFromHistory(subtaskId);// Удаляем таск из истории
            epicList.get(epicId).amendEpicStatus();
        } else {
            System.out.println("Сабтаска с ID: " + subtaskId + " не существует");
        }
    }

    @Override // 3.1. Получение списка подзадач для эпика.
    public List<Subtask> getEpicsSubtasks(int epicId) {
        List<Subtask> epicsSubtasks = new ArrayList<>();
        if (!epicList.containsKey(epicId)) {
            System.out.println("Нет эпика с ID: " + epicId);
        }
        for (Subtask subtask : subtaskList.values()) {
            if (subtask.getEpicId() == epicId) {
                epicsSubtasks.add(subtask);
            }
        }
        return epicsSubtasks;
    }

    @Override
    public int generateNewId() {
        return this.id++;
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }
}