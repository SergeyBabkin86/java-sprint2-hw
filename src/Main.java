import utilities.Managers;
import management.task.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(null, "Задача1", "Описание1", TaskStatus.NEW);
        Task task7 = new Task(null, "Задача7", "Описание7", TaskStatus.NEW);

        Epic epic2 = new Epic(null, "Эпик2", "ЭпикОписание2", null);
        Epic epic3 = new Epic(null, "Эпик3", "ЭпикОписание3", null);

        Subtask subtask4 = new Subtask(null, 2, "Подзадача4", "Описание4", TaskStatus.NEW);
        Subtask subtask5 = new Subtask(null, 2, "Подзадача5", "Описание5", TaskStatus.NEW);
        Subtask subtask6 = new Subtask(null, 2, "Подзадача6", "Описание6", TaskStatus.NEW);

        manager.addTask(task1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
        manager.addSubtask(subtask4);
        manager.addSubtask(subtask5);
        manager.addSubtask(subtask6);
        manager.addTask(task7);

        System.out.println("\nТЕСТ 1 Вызов задач в разном порядке и запрос истории");

        manager.getEpic(2);
        manager.getTask(1);
        manager.getTask(1);
        manager.getSubtask(5);
        manager.getTask(1);
        manager.getSubtask(4);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(5);
        manager.getEpic(2);
        manager.getEpic(2);
        manager.getTask(7);
        manager.getEpic(3);
        manager.getSubtask(4);
        manager.getSubtask(6);
        manager.getTask(1);

        System.out.println("\nЗапрос 1");
        for (Task historyTask : manager.history()) {
            System.out.println(historyTask);
        }

        manager.getSubtask(4);
        manager.getEpic(2);
        manager.getTask(1);
        manager.getSubtask(5);
        manager.getTask(1);
        manager.getSubtask(5);

        manager.getSubtask(6);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getEpic(3);
        manager.getEpic(2);
        manager.getEpic(2);
        manager.getTask(7);
        manager.getTask(1);
        manager.getSubtask(4);
        manager.getTask(1);

        System.out.println("\nЗапрос 2");
        for (Task historyTask : manager.history()) {
            System.out.println(historyTask);
        }

        System.out.println("\nТЕСТ 2 Удаление задачи из истории (удаляем задачу № 1.");
        manager.deleteTask(1);

        for (Task historyTask : manager.history()) {
            System.out.println(historyTask);
        }
        System.out.println("\nУдаление эпика с тремя подзадачами (удаляем эпик № 2)");
        manager.deleteEpic(2);

        for (Task historyTask : manager.history()) {
            System.out.println(historyTask);
        }
    }
}
