package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    public final ArrayList<Subtask> subtasksLinked = new ArrayList<>();

    public Epic(Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void amendEpicStatus() { // Метод для изменения статуса эпика.
        int subtaskDone = 0;
        int subtaskInProgress = 0;
        for (Subtask subtask : subtasksLinked) {
            if (TaskStatus.DONE == subtask.getStatus()) {
                subtaskDone++;
            } else if (TaskStatus.IN_PROGRESS == subtask.getStatus()) {
                subtaskInProgress++;
            }
        }
        if (subtasksLinked.size() == subtaskDone && !subtasksLinked.isEmpty()) {
            status = TaskStatus.DONE;
        } else if (subtaskInProgress > 0 || subtaskDone != 0) {
            status = TaskStatus.IN_PROGRESS;
        } else {
            status = TaskStatus.NEW;
        }
    }

    @Override
    public String toString() {
        return "\tEpic{" +
                "epicId=" + id +
                ", epicName='" + name + '\'' +
                ", epicDescription='" + description.length() + '\'' +
                ", epicStatus='" + status + '\'' +
                '}';
    }
}
