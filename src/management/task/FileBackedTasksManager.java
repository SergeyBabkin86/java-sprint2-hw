package management.task;

import Exceptions.ManagerSaveException;
import management.history.HistoryManager;
import tasks.*;
import utilities.Managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    File dir = new File("C:\\Users\\79268\\dev\\java-sprint2-hw\\test.csv");
    File file = new File(dir, "test.csv");

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTasksManager(HistoryManager defaultHistory) {
        super(defaultHistory);
    }

    @Override
    public List<Task> getAllTasksList() {
        return super.getAllTasksList();
    }

    @Override
    public void clearTaskLists() {
        super.clearTaskLists();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subTask) {
        super.addSubtask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public List<Subtask> getEpicsSubtasks(int epicId) {
        return super.getEpicsSubtasks(epicId);
    }

    @Override
    public List<Task> history() {
        return super.history();
    }

    @Override
    public int generateNewId() {
        return super.generateNewId();
    }

    public String historyToString(HistoryManager historyManager) {
        List<String> l = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            l.add(String.valueOf(task.getId()));
        }
        return String.join(",", l);
    }

    public void save() {
        try (FileWriter writer = new FileWriter(dir, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : taskList.values()) {
                writer.write(task + "\n");
            }
            for (Subtask subtask : subtaskList.values()) {
                writer.write(subtask + "\n");
            }
            for (Epic epic : epicList.values()) {
                writer.write(epic + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(this.historyManager));
            writer.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записть файл.");
        }
    }

    public static Task fromString(String value) {
        String[] splitValue = value.split(",");
        switch (TaskType.valueOf(splitValue[1])) {
            case TASK:
                return new Task(Integer.parseInt(splitValue[0]), splitValue[2], splitValue[4],
                        TaskStatus.valueOf(splitValue[3]));
            case EPIC:
                return new Epic(Integer.parseInt(splitValue[0]), splitValue[2], splitValue[4],
                        TaskStatus.valueOf(splitValue[3]));
            case SUBTASK:
                return new Subtask(Integer.parseInt(splitValue[0]), Integer.parseInt(splitValue[5]), splitValue[2],
                        splitValue[4], TaskStatus.valueOf(splitValue[3]));
        }
        return null;
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> list = new ArrayList<>();
        String[] splitValue = value.split(",");
        for (String s : splitValue) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s = br.readLine();   // Пропускаем первую строку
            if (s == null) {            // Проверяем не пустой ли файл
                throw new ManagerSaveException("В файле нет данных.");
            }
            while ((!s.isBlank())) {
                String line = br.readLine();
                s = line;
                if (!line.isBlank()) {
                    Task task = fromString(line);
                    if (task.getClass() == Task.class) {
                        fileBackedTasksManager.taskList.put(task.getId(), task);
                    } else if (task.getClass() == Epic.class) {
                        fileBackedTasksManager.epicList.put(task.getId(), (Epic) task);
                    } else {
                        fileBackedTasksManager.subtaskList.put(task.getId(), (Subtask) task);
                    }
                }
            }
            List<Integer> loadedHistory = historyFromString(br.readLine());

            for (Integer taskItem : loadedHistory) {
                if (fileBackedTasksManager.taskList.containsKey(taskItem)) {
                    Task task = fileBackedTasksManager.taskList.get(taskItem);
                    fileBackedTasksManager.historyManager.add(task);
                } else if (fileBackedTasksManager.epicList.containsKey(taskItem)) {
                    Epic epic = fileBackedTasksManager.epicList.get(taskItem);
                    fileBackedTasksManager.historyManager.add(epic);
                } else {
                    Subtask subtask = fileBackedTasksManager.subtaskList.get(taskItem);
                    fileBackedTasksManager.historyManager.add(subtask);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileBackedTasksManager;
    }

    public static void main(String[] args) {

        /* 1.Заведите несколько разных задач, эпиков и подзадач. */

        File dir = new File("C:\\Users\\79268\\dev\\java-sprint2-hw\\test.csv");

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory());

        Task task1 = new Task(null, "task1", "Description1", TaskStatus.NEW);
        Task task7 = new Task(null, "task7", "Description7", TaskStatus.NEW);
        Epic epic2 = new Epic(null, "epic2", "EpicDescription2", null);
        Epic epic3 = new Epic(null, "epic3", "EpicDescription3", null);
        Subtask subtask4 = new Subtask(null, 2, "Subtask4", "SbtDescription4", TaskStatus.NEW);
        Subtask subtask5 = new Subtask(null, 2, "Subtask5", "SbtDescription5", TaskStatus.NEW);
        Subtask subtask6 = new Subtask(null, 2, "Subtask6", "SbtDescription6", TaskStatus.NEW);

        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addEpic(epic2);
        fileBackedTasksManager.addEpic(epic3);
        fileBackedTasksManager.addSubtask(subtask4);
        fileBackedTasksManager.addSubtask(subtask5);
        fileBackedTasksManager.addSubtask(subtask6);
        fileBackedTasksManager.addTask(task7);

        /* 2. Запросите некоторые из них, чтобы заполнилась история просмотра. */

        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getSubtask(5);
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getSubtask(4);
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getSubtask(5);
        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getTask(7);
        fileBackedTasksManager.getEpic(3);
        fileBackedTasksManager.getSubtask(4);
        fileBackedTasksManager.getSubtask(6);
        fileBackedTasksManager.getTask(1);

        /* 3. Создайте новый FileBackedTasksManager менеджер из этого же файла. */

        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(dir);

        /* 4. Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи,
        /* которые были в старом, есть в новом менеджере.*/

        System.out.println("\nВызов объектов из опертивной паямяти --->");
        for (Object o : fileBackedTasksManager.getAllTasksList()) {
            System.out.println(o);
        }
        System.out.println("\nВызов истории из опертивной паямяти --->");
        for (Task historyTask : fileBackedTasksManager.history()) {
            System.out.println(historyTask);
        }

        System.out.println("\nВызов объектов из файла --->");
        for (Object o : fileBackedTasksManager1.getAllTasksList()) {
            System.out.println(o);
        }
        System.out.println("\nВызов истории из файла --->");
        for (Task historyTask : fileBackedTasksManager1.history()) {
            System.out.println(historyTask);
        }
    }
}
