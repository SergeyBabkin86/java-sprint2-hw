package management.task;

import management.history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

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
    public Task getTask(int id) {
        if (!taskList.containsKey(id)) {
            System.out.println("Задачи с ID: " + id + " не существует");
            return null;
        }
        historyManager.add(taskList.get(id)); // Добавляем в историю
        return taskList.get(id);
    }

    @Override // 2.3.2 Получение эпика по идентификатору.
    public Epic getEpic(int id) {
        if (!epicList.containsKey(id)) {
            System.out.println("Эпика с ID: " + id + " не существует");
            return null;
        }
        historyManager.add(epicList.get(id)); // Добавляем в историю
        return epicList.get(id);
    }

    @Override // 2.3.3 Получение сабтаска по идентификатору.
    public Subtask getSubtask(int id) {
        if (!subtaskList.containsKey(id)) {
            System.out.println("Сабтаска с ID: " + id + " не существует");
            return null;
        }
        historyManager.add(subtaskList.get(id)); // Добавляем в историю
        return subtaskList.get(id);
    }

    @Override // 2.4.1. Создание таска
    public Integer addTask(Task task) {
        if (task.getId() == null) {
            task.setId(generateNewId());
        } else if (taskList.containsKey(task.getId())) {
            System.out.printf("Перечень уже содержит Task с ID: %s", task.getId());
            return null;
        }
        if (isTasksCrossing(task.getStartTime())) {
            task.setStartTime(null);
            task.setDuration(null);
        }
        taskList.put(task.getId(), task);
        return task.getId();
    }

    @Override // 2.4.2. Создание эпика
    public Integer addEpic(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(generateNewId());
        } else if (epicList.containsKey(epic.getId())) {
            System.out.printf("Перечень уже содержит Epic с ID: %s", epic.getId());
            return null;
        }
        epicList.put(epic.getId(), epic);
        epic.amendEpicStatus();
        epic.setEpicTiming();
        return epic.getId();
    }

    @Override // 2.4.3. Создание сабтаска
    public Integer addSubtask(Subtask subtask) {
        if (subtask.getId() == null) {
            subtask.setId(generateNewId());
        } else if (subtaskList.containsKey(subtask.getId())) {
            System.out.printf("Перечень уже содержит Subtask с ID: %s", subtask.getId());
            return null;
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
    public void updateTask(Task task) {
        if (task.getId() == null || !taskList.containsKey(task.getId())) {
            System.out.println("Невозможно обновить task (не указан ID или такой task отсутсвует)");
            return;
        }
        if (isTasksCrossing(task.getStartTime())) {
            task.setStartTime(null);
            task.setDuration(null);
        }
        taskList.put(task.getId(), task);
    }

    @Override // 2.5.2. Обновление эпика
    public void updateEpic(Epic epic) {
        if (epic.getId() == null || !epicList.containsKey(epic.getId())) {
            System.out.println("Невозможно обновить epic (не указан ID или такой epic отсутсвует)");
            return;
        }
        epicList.put(epic.getId(), epic);
        epicList.get(epic.getId()).subtasksLinked.clear();
        epic.subtasksLinked.addAll(getEpicsSubtasks(epic.getId()));
        epic.amendEpicStatus();
        epic.setEpicTiming();
    }

    @Override // 2.5.3. Обновление сабтаска
    public void updateSubtask(Subtask subtask) {
        if (subtask.getId() == null || !subtaskList.containsKey(subtask.getId())) {
            System.out.println("Невозможно обновить subtask (не указан ID или такой subtask отсутсвует)");
            return;
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
    public void deleteTask(int taskId) {
        if (taskList.containsKey(taskId)) {
            taskList.remove(taskId);
            historyManager.remove(taskId); // Удаляем таск из истории
        } else {
            System.out.println("Задачи с ID: " + taskId + " не существует");
        }
    }

    @Override // 2.6.2. Удаление эпика по идентификатору
    public void deleteEpic(int epicId) {
        Set<Integer> keysToRemove = new HashSet<>();
        if (epicList.containsKey(epicId)) {
            for (Integer subtaskKey : subtaskList.keySet()) {
                if (subtaskList.get(subtaskKey).getEpicId() == epicId) {
                    keysToRemove.add(subtaskList.get(subtaskKey).getId());
                    historyManager.remove(subtaskList.get(subtaskKey).getId());
                }
            }
            subtaskList.keySet().removeAll(keysToRemove);
            epicList.remove(epicId);
            historyManager.remove(epicId);
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
            epicList.get(epicId).amendEpicStatus();
            epicList.get(epicId).setEpicTiming();
            historyManager.remove(subtaskId); // Удаляем таск из истории
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
                System.out.printf("Устновленное время выполнения задачи: %s " +
                                "пересекатеся со временем выполнения задачи c ID: %s (%s - %s)\n",
                        startTime, taskCross.getId(),
                        taskCross.getStartTime(), taskCross.getEndTime());
            flag = true;
        }
        return flag;
    }
}