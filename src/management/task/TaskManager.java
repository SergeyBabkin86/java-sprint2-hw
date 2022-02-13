package management.task;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Object> getAllTasksList(); // 2.1 Получение списка всех задач.

    void clearTaskLists(); // 2.2 Удаление всех задач.

    Task getTask(int id); // 2.3.2 Получение задачи по идентификатору.

    Epic getEpic(int id); // 2.3.1 Получение эпика по идентификатору.

    Subtask getSubtask(int id); // 2.3.3 Получение сабтаска по идентификатору.

    void addTask(Task task); // 2.4.1. Создание таска

    void addEpic(Epic epic); // 2.4.2. Создание эпика

    void addSubtask(Subtask subTask); // 2.4.3. Создание сабтаска

    void updateTask(Task task); // 2.5.1. Обновление таска

    void updateEpic(Epic epic); // 2.5.2. Обновление эпика

    void updateSubtask(Subtask subtask); // 2.5.3. Обновление сабтаска

    void deleteTask(int taskId); // 2.6.1. Удаление задачи по идентификатору

    void deleteEpic(int epicId); // 2.6.2. Удаление эпика по идентификатору

    void deleteSubtask(int subtaskId); // 2.6.3. Удаление сабтаска по идентификатору

    List<Subtask> getEpicsSubtasks(int epicId); // 3.1. Получение списка подзадач для эпика.

    List<Task> history(); // Спр. 3 Получение истории

    int generateNewId();
}
