import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class Manager {
    private int id = 1; // Для присвоения ID.

    HashMap<Integer, Task> taskList = new HashMap<>(); // Для хранения отдельных задач
    HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    HashMap<Integer, Epic> epicList = new HashMap<>();

    public ArrayList<Object> getAllTasksList() { // 2.1 Получение списка всех задач.
        ArrayList<Object> allTasksList = new ArrayList<>();
        allTasksList.addAll(taskList.values());
        allTasksList.addAll(epicList.values());
        allTasksList.addAll(subtaskList.values());
        return allTasksList;
    }

    public void clearTaskLists() { // 2.2 Удаление всех задач.
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
        System.out.println("Все списки задач очищены.");
    }

    public Task getTask(int id) { // 2.3.2 Получение задачи по идентификатору.
        if (!taskList.containsKey(id)) {
            System.out.println("Задачи с ID: " + id + " не существует");
        }
        return taskList.get(id);
    }

    public Epic getEpic(int id) { // 2.3.1 Получение эпика по идентификатору.
        if (!epicList.containsKey(id)) {
            System.out.println("Эпика с ID: " + id + " не существует");
        }
        return epicList.get(id);
    }

    public Subtask getSubtask(int id) { // 2.3.3 Получение сабтаска по идентификатору.
        if (!subtaskList.containsKey(id)) {
            System.out.println("Сабтаска с ID: " + id + " не существует");
        }
        return subtaskList.get(id);
    }

    public void addTask(Task task) { // 2.4.1. Создание таска
        task.setId(generateNewId());
        taskList.put(task.getId(), task);
    }

    public void addEpic(Epic epic) { // 2.4.2. Создание эпика
        epic.setId(generateNewId());
        epicList.put(epic.getId(), epic);
        epic.amendEpicStatus();
    }

    public void addSubtask(Subtask subTask) { // 2.4.3. Создание сабтаска
        int epicId = subTask.getEpicId();
        subTask.setId(generateNewId());
        subtaskList.put(subTask.getId(), subTask);
        epicList.get(epicId).subtasksLinked.add(subTask);
        epicList.get(epicId).amendEpicStatus();
    }

    public void updateTask(Task task) { // 2.5.1. Обновление таска
        Integer taskId = task.getId();
        taskList.replace(taskId, task);
    }

    public void updateEpic(Epic epic) { // 2.5.2. Обновление эпика
        int epicId = epic.getId();
        epicList.replace(epicId, epic);
        epic.subtasksLinked.addAll(getEpicsSubtasks(epicId));
        epic.amendEpicStatus();
    }

    public void updateSubtask(Subtask subtask) { // 2.5.3. Обновление сабтаска
        int epicId = subtask.getEpicId();
        int indexInEpic = epicList.get(epicId).subtasksLinked.indexOf(subtask);
        epicList.get(epicId).subtasksLinked.set(indexInEpic, subtask);
        subtaskList.replace(subtask.getId(), subtask);
        epicList.get(epicId).amendEpicStatus();
    }

    public void deleteTask(int taskId) { // 2.6.1. Удаление задачи по идентификатору
        if (taskList.containsKey(taskId)) {
            taskList.remove(taskId);
        } else {
            System.out.println("Задачи с ID: " + taskId + " не существует");
        }
    }

    public void deleteEpic(int epicId) { // 2.6.2. Удаление эпика по идентификатору
        if (epicList.containsKey(epicId)) {
            epicList.remove(epicId);
            for (Integer subtaskKey : subtaskList.keySet()) { // Вместе с эпиком удаляем подзадачи
                if (subtaskList.get(subtaskKey).getEpicId() == epicId) {
                    subtaskList.remove(subtaskKey);
                }
            }
        } else {
            System.out.println("Эпика с ID: " + epicId + " не существует");
        }
    }

    public void deleteSubtask(int subtaskId) { // 2.6.3. Удаление сабтаска по идентификатору
        if (subtaskList.containsKey(subtaskId)) {
            int epicId = subtaskList.get(subtaskId).getEpicId();
            epicList.get(epicId).subtasksLinked.remove(subtaskList.get(subtaskId));
            subtaskList.remove(subtaskId);
            epicList.get(epicId).amendEpicStatus();
        } else {
            System.out.println("Сабтаска с ID: " + subtaskId + " не существует");
        }
    }

    public ArrayList<Subtask> getEpicsSubtasks(int epicId) { // 3.1. Получение списка подзадач для эпика.
        ArrayList<Subtask> epicsSubtasks = new ArrayList<>();
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

    private int generateNewId() {
        return this.id++;
    }
}