public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Задача1", "Описание1");
        Task task7 = new Task("Задача7", "Описание7");
        Epic epic2 = new Epic("Эпик2");
        Epic epic3 = new Epic("Эпик3");
        SubTask subTask4 = new SubTask(2, "Описание4");
        SubTask subTask5 = new SubTask(2, "Описание5");
        SubTask subTask6 = new SubTask(3, "Описание6");
        manager.addTask(task1, 0);
        manager.addTask(epic2, 0);
        manager.addTask(epic3, 0);
        manager.addTask(subTask4, 2);
        manager.addTask(subTask5, 2);
        manager.addTask(subTask6, 3);
        manager.addTask(task7, 0);


        System.out.println("\n2.1 Проверяем работу по получение списка всех задач.");
        manager.printTaskList();

        System.out.println("\n2.3 Проверяем олучение по идентификатору. ID=2");
        System.out.println(manager.getTask(2));

        System.out.println("\n2.5. Обновление. Изменяем один из эпиков и субтасков и проверяем изменения");
        Task revisedTask = new Task(7, "НоваяЗадача7", "НовоеОписание7", "DONE");
        manager.updateTask(revisedTask);
        Epic revisedEpic = new Epic(3, "НовыйЭпик3");
        manager.updateEpic(revisedEpic);
        SubTask revisedSubTask = new SubTask(5, 2, "НовоеОписание5", "DONE");
        manager.updateSubTask(revisedSubTask);
        manager.printTaskList();

        System.out.println("\n2.6. Удаление. Удаляем эпик 3, задачу 7 и подзадачу 4 (для проверки изменения статуса)");
        manager.deleteTask(3);
        manager.deleteTask(4);
        manager.deleteTask(7);
        manager.printTaskList();

        System.out.println("\n3.1. Получение списка всех подзадач для эпика 2. ");
        manager.getEpicSubTasks(2);
    }
}
