import java.util.HashMap;

public class Manager {
    private int id = 1; // Для присвоения ID.

    HashMap<Integer, Task> taskList = new HashMap<>(); // Для хранения отдельных задач
    HashMap<Integer, SubTask> subTaskList = new HashMap<>();
    HashMap<Integer, Epic> epicList = new HashMap<>();

    /* 2.1 Получение списка всех задач. Не печататет сабтаск листо отдельно т.к. сабтаск всегда привязан к эпику
    (сабтаск не имеет смысла без эпика) */
    void printTaskList() {
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
                for (Integer subTaskKey : epic.subTaskReference.keySet()) {
                    System.out.println(subTaskList.get(subTaskKey));
                }
            }
        } else {
            System.out.println("Список эпиков пуст.");
        }
    }

    void clearTaskList() { // 2.2 Удаление всех задач.
        taskList.clear();
        epicList.clear();
        subTaskList.clear();
        System.out.println("Все списки задач очищены.");
    }

    Object getTask(int id) { // 2.3 Получение по идентификатору.
        if (taskList.containsKey(id)) {
            taskList.get(id);
            return taskList.get(id);
        } else if (epicList.containsKey(id)) {
            return epicList.get(id);
        } else if (subTaskList.containsKey(id)) {
            return subTaskList.get(id);
        } else {
            System.out.println("Задачи с ID: " + id + " не существует");
        }
        return null;
    }

    void addTask(Object object, Integer epicId) { // 2.4. Создание объектов
        if (object.getClass() == Epic.class) {
            Epic epicObject = (Epic) object;
            epicObject.setEpicId(this.id);
            epicList.put(this.id, epicObject);
        } else if (object.getClass() == Task.class) {
            Task taskObject = (Task) object;
            taskObject.setTaskID(this.id);
            taskList.put(this.id, taskObject);
        } else if (object.getClass() == SubTask.class) {
            SubTask subTaskObject = (SubTask) object; // Приводим, чтобы получить доступ к полям.
            subTaskObject.setSubTaskId(this.id); // Присваеваем id субтаску.
            subTaskObject.setEpicId(epicId); // Присваиваем epicId сабтаску (для привязки к эпику).
            subTaskList.put(this.id, subTaskObject); // Помещам в cабтаскHashmap.
            updateSubTaskRef(epicId); // Обновляем subTaskReference эпика, к которому привязан subTask
        }
        this.id++;
    }

    public void updateTask(Task task) { // 2.5.1. Обновление таска
        Integer taskId = task.getTaskID();
        taskList.replace(taskId, task);
    }

    public void updateEpic(Epic epic) { // 2.5.2. Обновление эпика
        int epicId = epic.getEpicId();
        epicList.replace(epicId, epic);
        updateSubTaskRef(epicId);
    }

    public void updateSubTask(SubTask subTask) { // 2.5.3. Обновление сабтаска
        int epicId = subTask.getEpicId();
        subTaskList.replace(subTask.getSubTaskId(), subTask);
        updateSubTaskRef(epicId);
    }

    void deleteTask(int id) { // 2.6 Удаление по идентификатору
        if (taskList.containsKey(id)) {
            taskList.remove(id);
        } else if (epicList.containsKey(id)) {
            epicList.remove(id);
        } else if (subTaskList.containsKey(id)) {
            int epicId = subTaskList.get(id).getEpicId();
            subTaskList.remove(id);
            updateSubTaskRef(epicId);
        } else {
            System.out.println("Задачи с ID: " + id + " не существует");
        }
    }

    void getEpicSubTasks(int id) { // 3.1. Получение списка всех подзадач определённого эпика.
        System.out.println("Список задача для эпика с ID " + id);
        for (Integer subTask : epicList.get(id).subTaskReference.keySet()) { // Получаем ключи сабтасков из объекта эпик
            System.out.println(subTaskList.get(subTask));
        }
    }
        // Метод для обновления данных в subTaskReference при внесении, изменении и удале нии субтасков и эпиков.
        private void updateSubTaskRef(int epicId) {
        Epic epicObject = epicList.get(epicId);
        epicObject.subTaskReference.clear();
        SubTask subTaskObject;
        int id;
        if (epicList.containsKey(epicId)) {
            for (Integer subTaskKey : subTaskList.keySet()) {
                subTaskObject = subTaskList.get(subTaskKey);
                id = subTaskObject.getEpicId();
                if (id == epicId) {
                    epicObject.subTaskReference.put(subTaskKey, subTaskObject.getStatus());
                }
            }
        } else {
            System.out.println("Такого эпика нет " + epicId);
        }
        epicList.get(epicId).amendEpicStatus();
    }
}