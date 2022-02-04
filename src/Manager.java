import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;

public class Manager {
    private int id = 1; // Для присвоения ID.

    HashMap<Integer, Task> taskList = new HashMap<>(); // Для хранения отдельных задач
    HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    HashMap<Integer, Epic> epicList = new HashMap<>();

    public void printTaskList() { // 2.1 Получение списка всех задач.
        if (!taskList.isEmpty()) {
            for (Integer task : taskList.keySet()) {
                System.out.println(taskList.get(task));
            }
        } else {
            System.out.println("Список задач пуст.");
        }
        if (!epicList.isEmpty()) {
            for (Integer epics : epicList.keySet()) {
                Epic epic = epicList.get(epics);
                System.out.println(epic);
                for (int i = 0; i < epic.subtasksLinked.size(); i++) {
                    System.out.println(epic.subtasksLinked.get(i));
                }
            }
        } else {
            System.out.println("Список эпиков пуст.");
        }
    }

    public void clearTaskList() { // 2.2 Удаление всех задач.
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
        System.out.println("Все списки задач очищены.");
    }

    public Object getTask(int id) { // 2.3 Получение по идентификатору.
        if (taskList.containsKey(id)) {
            taskList.get(id);
            return taskList.get(id);
        } else if (epicList.containsKey(id)) {
            return epicList.get(id);
        } else if (subtaskList.containsKey(id)) {
            return subtaskList.get(id);
        } else {
            System.out.println("Задачи с ID: " + id + " не существует");
        }
        return null;
    }

    public void addEpic(Epic epic) { // 2.4.1. Создание эпика
        epic.setId(generateNewId());
        epicList.put(epic.getId(), epic);
        epic.amendEpicStatus();
    }

    public void addTask(Task task) { // 2.4.2. Создание таска
        task.setId(generateNewId());
        taskList.put(task.getId(), task);
    }

    public void addSubTask(Subtask subTask) { // 2.4.2. Создание сабтаска
        subTask.setId(generateNewId());
        subtaskList.put(subTask.getId(), subTask);
        updateSubtaskLink(subTask.getEpicId());
    }

    public void updateTask(Task task) { // 2.5.1. Обновление таска
        Integer taskId = task.getId();
        taskList.replace(taskId, task);
    }

    public void updateEpic(Epic epic) { // 2.5.2. Обновление эпика
        int epicId = epic.getId();
        epicList.replace(epicId, epic);
        updateSubtaskLink(epicId);
    }

    public void updateSubTask(Subtask subTask) { // 2.5.3. Обновление сабтаска
        int epicId = subTask.getEpicId();
        subtaskList.replace(subTask.getId(), subTask);
        updateSubtaskLink(epicId);
    }

    public void deleteTask(int id) { // 2.6 Удаление по идентификатору
        if (taskList.containsKey(id)) {
            taskList.remove(id);
        } else if (epicList.containsKey(id)) {
            epicList.remove(id);
        } else if (subtaskList.containsKey(id)) {
            int epicId = subtaskList.get(id).getEpicId();
            subtaskList.remove(id);
            updateSubtaskLink(epicId);
        } else {
            System.out.println("Задачи с ID: " + id + " не существует");
        }
    }

    public void getEpicSubTasks(int epicId) { // 3.1. Получение списка всех подзадач определённого эпика.
        System.out.println("Список задач для эпика с ID " + epicId);
        Epic epic = epicList.get(epicId);
        for (int i = 0; i < epic.subtasksLinked.size(); i++) {
            System.out.println(epic.subtasksLinked.get(i));
        }
    }

    // Метод для обновления данных в subTaskReference при внесении, изменении и удалении субтасков и эпиков.
    private void updateSubtaskLink(int epicId) {
        Epic epicObject = epicList.get(epicId);
        epicObject.subtasksLinked.clear();
        Subtask subtaskObject;
        int id;
        if (epicList.containsKey(epicId)) {
            for (Integer subTaskKey : subtaskList.keySet()) {
                subtaskObject = subtaskList.get(subTaskKey);
                id = subtaskObject.getEpicId();
                if (id == epicId) {
                    epicObject.subtasksLinked.add(subtaskObject);
                }
            }
        } else {
            System.out.println("Такого эпика нет " + epicId);
        }
        epicList.get(epicId).amendEpicStatus();
    }

    private int generateNewId() {
        return this.id++;
    }
}