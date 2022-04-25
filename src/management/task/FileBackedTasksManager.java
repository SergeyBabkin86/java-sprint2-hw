package management.task;

import Exceptions.ManagerSaveException;
import management.history.HistoryManager;
import tasks.*;
import utilities.Managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTasksManager(HistoryManager defaultHistory, File file) {
        super(defaultHistory);
        this.file = file;
    }

    @Override
    public List<Task> getAllTasksList() {
        return super.getAllTasksList();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
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
    public Integer addTask(Task task) {
        Integer taskId = super.addTask(task);
        save();
        return taskId;
    }

    @Override
    public Integer addEpic(Epic epic) {
        Integer epicId = super.addEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        Integer subtaskId = super.addSubtask(subtask);
        save();
        return subtaskId;
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

    @Override
    public boolean isTasksCrossing(LocalDateTime startTime) {
        return super.isTasksCrossing(startTime);
    }

    public String historyToString(HistoryManager historyManager) {
        List<String> l = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            l.add(String.valueOf(task.getId()));
        }
        return String.join(",", l);
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic,startTime,duration,endTime\n");
            for (Task task : getAllTasksList()) {
                writer.write(task + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(this.historyManager));
            writer.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записть файл.");
        }
    }

    public static Task taskFromString(String value) {
        Task taskFromString;
        String[] splitValue = value.split(",");
        switch (TaskType.valueOf(splitValue[1])) {
            case TASK:
                taskFromString = new Task(Integer.parseInt(splitValue[0]),
                        splitValue[2],
                        splitValue[4],
                        TaskStatus.valueOf(splitValue[3]));
                break;
            case EPIC:
                taskFromString = new Epic(Integer.parseInt(splitValue[0]),
                        splitValue[2],
                        splitValue[4],
                        TaskStatus.valueOf(splitValue[3]));
                break;
            case SUBTASK:
                taskFromString = new Subtask(Integer.parseInt(splitValue[0]),
                        Integer.parseInt(splitValue[5]),
                        splitValue[2],
                        splitValue[4],
                        TaskStatus.valueOf(splitValue[3]));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + TaskType.valueOf(splitValue[1]));
        }
        if (!splitValue[6].equals("null") && taskFromString.getClass() != Epic.class) {
            taskFromString.setStartTime(LocalDateTime.parse(splitValue[6]));

        }
        if (!splitValue[7].equals("null") && taskFromString.getClass() != Epic.class) {
            taskFromString.setDuration(Duration.parse(splitValue[7]));
        }
        return taskFromString;
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
                System.out.println("В файле нет данных");
            } else {
                while ((!s.isBlank())) {
                    String line = br.readLine();
                    s = line;
                    if (!line.isBlank()) {
                        Task task = taskFromString(line);
                        if (task.getClass() == Task.class) {
                            fileBackedTasksManager.addTask(task);
                        } else if (task.getClass() == Epic.class) {
                            fileBackedTasksManager.addEpic((Epic) task);
                        } else {
                            fileBackedTasksManager.addSubtask((Subtask) task);
                        }
                    }
                }
            }
            String historyRecord = br.readLine();
            if (historyRecord == null) {
                System.out.println("В файле нет данных по истории просмотров");
            } else {
                for (Integer taskId : historyFromString(historyRecord)) {
                    for (Task task : fileBackedTasksManager.getAllTasksList()) {
                        if (taskId.equals(task.getId())) {
                            fileBackedTasksManager.historyManager.add(task);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileBackedTasksManager;
    }

    public static void main(String[] args) {

        /* 1.Заведите несколько разных задач, эпиков и подзадач. */
        File file = new File("C:\\Users\\79268\\dev\\java-sprint2-hw\\sprint5.csv");

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);

        Task task1 = new Task("task1", "Description1", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2022, 4, 16, 13, 45));
        task1.setDuration(Duration.ofHours(10));
        Task task7 = new Task("task7", "Description7", TaskStatus.NEW);
        task7.setStartTime(LocalDateTime.of(2022, 4, 13, 13, 45));
        task7.setDuration(Duration.ofHours(10));
        Epic epic2 = new Epic("epic2", "EpicDescription2", null);
        Epic epic3 = new Epic("epic3", "EpicDescription3", null);
        Subtask subtask4 = new Subtask(2, "Subtask4", "SbtDescription4", TaskStatus.NEW);
        subtask4.setStartTime(LocalDateTime.of(2022, 4, 13, 12, 45));
        subtask4.setDuration(Duration.ofHours(10));
        Subtask subtask5 = new Subtask(2, "Subtask5", "SbtDescription5", TaskStatus.NEW);
        subtask5.setStartTime(LocalDateTime.of(2022, 4, 11, 13, 45));
        subtask5.setDuration(Duration.ofHours(10));
        Subtask subtask6 = new Subtask(2, "Subtask6", "SbtDescription6", TaskStatus.NEW);
        subtask6.setStartTime(LocalDateTime.of(2022, 4, 19, 13, 45));
        subtask6.setDuration(Duration.ofHours(10));

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

        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);

        /* 4. Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи,
        /* которые были в старом, есть в новом менеджере.*/

        System.out.println("\nВызов объектов из опертивной паямяти --->");
        for (Task task : fileBackedTasksManager.getAllTasksList()) {
            System.out.println(task);
        }

        System.out.println("\nВызов объектов из файла --->");
        for (Task task : fileBackedTasksManager1.getAllTasksList()) {
            System.out.println(task);
        }

        System.out.println("\nВызов истории из опертивной паямяти --->");
        for (Task historyTask : fileBackedTasksManager.history()) {
            System.out.println(historyTask);
        }

        System.out.println("\nВызов истории из файла --->");
        for (Task historyTask : fileBackedTasksManager1.history()) {
            System.out.println(historyTask);
        }
    }
}
