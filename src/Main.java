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
        Subtask subtask6 = new Subtask(null, 3, "Подзадача6", "Описание6", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
        manager.addSubtask(subtask4);
        manager.addSubtask(subtask5);
        manager.addSubtask(subtask6);
        manager.addTask(task7);

        System.out.println("\n2.1 Проверяем работу метода getAllTasksList().");
        for (Object object : manager.getAllTasksList()) {
            System.out.println(object);
        }

        System.out.println("\n2.3 Проверяем получение по идентификатору.");
        System.out.println(manager.getEpic(2));
        System.out.println(manager.getTask(7));
        System.out.println(manager.getSubtask(6));

        System.out.println("\n2.5. Обновление. Изменяем задачу7, эпик3, сабтаск5 и проверяем внедерение изменений");
        Task revisedTask = new Task(7, "НоваяЗадача7", "НовоеОписание7", TaskStatus.DONE);
        manager.updateTask(revisedTask);
        System.out.println(manager.getTask(7));
        Epic revisedEpic = new Epic(2, "НовыйЭпик3", "НовоеЭпическоеОписание3", null);
        manager.updateEpic(revisedEpic);
        System.out.println(manager.getEpic(3));
        Subtask revisedSubtask = new Subtask(5, 2, "НовоеНазвание5", "НовоеОписание5", TaskStatus.DONE);
        manager.updateSubtask(revisedSubtask);
        System.out.println(manager.getEpic(2));
        System.out.println(manager.getEpicsSubtasks(2));

        System.out.println("\nИСТОРИЯ ДО УДАЛЕНИЯ:");
        manager.getTask(1);
        manager.getTask(1);
        manager.getTask(1);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getEpic(2);
        manager.getEpic(2);
        manager.getEpic(3);
        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getSubtask(6);
        manager.getTask(7);

        System.out.println();
        for (Task historyTask : manager.history()) {
            System.out.println(historyTask);
        }

        System.out.println("\n2.6. Удаление. Удаляем эпик 3, задачу 7 и подзадачу 4 (для проверки изменения статуса)");
        manager.deleteEpic(3);
        manager.deleteSubtask(4);
        manager.deleteTask(7);

        // Проверяем работу по удалению
        for (Object object : manager.getAllTasksList()) {
            System.out.println(object);
        }

        System.out.println("\nИСТОРИЯ ПОСЛЕ УДАЛЕНИЯ:");

        System.out.println();
        for (Task historyTask : manager.history()) {
            System.out.println(historyTask);
        }

        manager.clearTaskLists();
    }
}
