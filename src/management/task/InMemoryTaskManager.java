package management.task;

import management.history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id = 1;
    protected final HistoryManager historyManager;

    protected final Map<Integer, Task> taskList = new HashMap<>();
    protected final Map<Integer, Subtask> subtaskList = new HashMap<>();
    protected final Map<Integer, Epic> epicList = new HashMap<>();

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override // 2.1 Получение списка всех задач.
    public List<Task> getAllTasksList() {
        List<Task> allTasksList = new ArrayList<>();
        allTasksList.addAll(taskList.values());
        allTasksList.addAll(epicList.values());
        allTasksList.addAll(subtaskList.values());
        return allTasksList;
    }

    @Override
    public Set<Task> getPrioritizedTasks() { // Вернуть список задач и подзадач в заданном порядке
        Set<Task> prioritizedTasks = new TreeSet<>((o1, o2) -> {
            if (o1.getId().equals(o2.getId())) return 0;
            if (o1.getStartTime() == null) return 1;
            if (o2.getStartTime() == null) return -1;
            if (o1.getStartTime().compareTo(o2.getStartTime()) == 0) return 1;
            return o1.getStartTime().compareTo(o2.getStartTime());
        });
        prioritizedTasks.addAll(getAllTasksList());
        return prioritizedTasks;
    }

    @Override // 2.2 Удаление всех задач.
    public void clearTaskLists() {
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
        System.out.println("Все списки задач очищены.");
    }

    @Override // 2.3.1 Получение задачи по идентификатору.
    public Task getTask(int id) throws IOException {
        if (taskList.containsKey(id)) {
            historyManager.add(taskList.get(id)); // Добавляем в историю
            return taskList.get(id);
        } else {
            throw new IOException(String.format("Задачи с ID: %s не существует", id));
        }
    }

    @Override // 2.3.2 Получение эпика по идентификатору.
    public Epic getEpic(int id) throws IOException {
        if (epicList.containsKey(id)) {
            historyManager.add(epicList.get(id)); // Добавляем в историю
            return epicList.get(id);
        } else {
            throw new IOException(String.format("Эпика с ID: %s не существует", id));
        }
    }

    @Override // 2.3.3 Получение сабтаска по идентификатору.
    public Subtask getSubtask(int id) throws IOException {
        if (subtaskList.containsKey(id)) {
            historyManager.add(subtaskList.get(id)); // Добавляем в историю
            return subtaskList.get(id);
        }
        throw new IOException(String.format("Сабтаска с ID: %s не существует", id));
    }

    @Override // 2.4.1. Создание таска
    public Integer addTask(Task task) throws IOException {
        if (task.getId() == null) {
            task.setId(generateNewId());
        } else {
            if (taskList.containsKey(task.getId())) {
                throw new IOException(String.format("Невозможно добавить задачу. " +
                        "Задача с ID: %s уже существует", task.getId()));
            }
        }
        if (isTasksCrossing(task.getStartTime())) {
            task.setStartTime(null);
            task.setDuration(null);
        }
        taskList.put(task.getId(), task);
        return task.getId();
    }

    @Override // 2.4.2. Создание эпика
    public Integer addEpic(Epic epic) throws IOException {
        if (epic.getId() == null) {
            epic.setId(generateNewId());
        } else {
            if (epicList.containsKey(epic.getId())) {
                throw new IOException(String.format("Невозможно добавить эпик. " +
                        "Эпик с ID: %s уже существует", epic.getId()));
            }
        }
        epicList.put(epic.getId(), epic);
        epicList.get(epic.getId()).subtasksLinked.clear();
        epic.subtasksLinked.addAll(getEpicsSubtasks(epic.getId()));
        epic.amendEpicStatus();
        epic.setEpicTiming();
        return epic.getId();
    }

    @Override // 2.4.3. Создание сабтаска
    public Integer addSubtask(Subtask subtask) throws IOException {
        if (subtask.getId() == null) {
            subtask.setId(generateNewId());
        } else {
            if (subtaskList.containsKey(subtask.getId())) {
                throw new IOException(String.format("Невозможно добавить подзадачу. " +
                        "Подзадача с ID: %s уже существует", subtask.getId()));
            }
        }
        if (isTasksCrossing(subtask.getStartTime())) {
            subtask.setStartTime(null);
            subtask.setDuration(null);
        }
        int epicId = subtask.getEpicId();
        subtaskList.put(subtask.getId(), subtask);
        epicList.get(epicId).subtasksLinked.add(subtask);
        epicList.get(epicId).amendEpicStatus();
        epicList.get(epicId).setEpicTiming();
        return subtask.getId();
    }

    @Override // 2.5.1. Обновление таска
    public void updateTask(Task task) throws IOException {
        if (task.getId() == null || !taskList.containsKey(task.getId())) {
            throw new IOException("Невозможно обновить задачу: не указан ID || задача отсутствует.");
        }
        if (isTasksCrossing(task.getStartTime())) {
            task.setStartTime(null);
            task.setDuration(null);
        }
        taskList.put(task.getId(), task);
    }

    @Override // 2.5.2. Обновление эпика
    public void updateEpic(Epic epic) throws IOException {
        if (epic.getId() == null || !epicList.containsKey(epic.getId())) {
            throw new IOException("Невозможно обновить эпик: не указан ID || эпик отсутствует.");
        }
        epicList.put(epic.getId(), epic);
        epicList.get(epic.getId()).subtasksLinked.clear();
        epic.subtasksLinked.addAll(getEpicsSubtasks(epic.getId()));
        epic.amendEpicStatus();
        epic.setEpicTiming();
    }

    @Override // 2.5.3. Обновление сабтаска
    public void updateSubtask(Subtask subtask) throws IOException {
        if (subtask.getId() == null || !subtaskList.containsKey(subtask.getId())) {
            throw new IOException("Невозможно обновить подзадачу: не указан ID || подзадача отсутствует.");
        }
        if (isTasksCrossing(subtask.getStartTime())) {
            subtask.setStartTime(null);
            subtask.setDuration(null);
        }
        int epicId = subtask.getEpicId();
        subtaskList.put(subtask.getId(), subtask);
        epicList.get(epicId).subtasksLinked.clear();
        epicList.get(epicId).subtasksLinked.addAll(getEpicsSubtasks(epicId));
        epicList.get(epicId).amendEpicStatus();
        epicList.get(epicId).setEpicTiming();
    }

    @Override // 2.6.1. Удаление задачи по идентификатору
    public void deleteTask(int taskId) throws IOException {
        if (!taskList.containsKey(taskId)) {
            throw new IOException(String.format("Задачи с ID: %s не существует", taskId));
        }
        taskList.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override // 2.6.2. Удаление эпика по идентификатору
    public void deleteEpic(int epicId) throws IOException {
        Set<Integer> keysToRemove = new HashSet<>();
        if (!epicList.containsKey(epicId)) {
            throw new IOException(String.format("Эпика с ID: %s не существует", epicId));
        }
        for (Integer subtaskKey : subtaskList.keySet()) {
            if (subtaskList.get(subtaskKey).getEpicId() == epicId) {
                keysToRemove.add(subtaskList.get(subtaskKey).getId());
                historyManager.remove(subtaskList.get(subtaskKey).getId());
            }
        }
        subtaskList.keySet().removeAll(keysToRemove);
        epicList.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override // 2.6.3. Удаление сабтаска по идентификатору
    public void deleteSubtask(int subtaskId) throws IOException {
        if (!subtaskList.containsKey(subtaskId)) {
            throw new IOException(String.format("Подзадачи с ID: %s не существует", subtaskId));
        }
        int epicId = subtaskList.get(subtaskId).getEpicId();
        epicList.get(epicId).subtasksLinked.remove(subtaskList.get(subtaskId));
        subtaskList.remove(subtaskId);
        epicList.get(epicId).amendEpicStatus();
        epicList.get(epicId).setEpicTiming();
        historyManager.remove(subtaskId);
    }

    @Override // 3.1. Получение списка подзадач для эпика.
    public List<Subtask> getEpicsSubtasks(int epicId) throws IOException {
        List<Subtask> epicsSubtasks = new ArrayList<>();
        if (!epicList.containsKey(epicId)) {
            throw new IOException(String.format("Невозможно получить перечень подзадач, " +
                    "т.к. эпик с ID: %s не существует.", epicId));
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
        for (Task task : getAllTasksList()) {
            if (task.getId() > this.id) {
                this.id = task.getId() + 1;
            }
        }
        return this.id++;
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    @Override
    public boolean isTasksCrossing(LocalDateTime startTime) {
        boolean flag = false;
        List<Task> crossingTasksList = new ArrayList<>();
        for (Task task : getPrioritizedTasks()) {
            if (task.getStartTime() != null && startTime != null) {
                if (task.getStartTime().isBefore(startTime) && task.getEndTime().isAfter(startTime)) {
                    crossingTasksList.add(task);
                }
            }
        }
        if (!crossingTasksList.isEmpty()) {
            for (Task taskCross : crossingTasksList)
                System.out.printf("Установленное время выполнения задачи: %s " +
                                "пересекается со временем выполнения задачи c ID: %s (%s - %s)\n",
                        startTime, taskCross.getId(),
                        taskCross.getStartTime(), taskCross.getEndTime());
            flag = true;
        }
        return flag;
    }
}