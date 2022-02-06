package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Subtask> subtasksLinked = new ArrayList<>();

    public Epic(Integer id, String name, String description, String status) {
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
            if (subtask.getStatus().equals("DONE")) {
                subtaskDone++;
            } else if (subtask.getStatus().equals("IN_PROGRESS")) {
                subtaskInProgress++;
            }
        }
        if (subtasksLinked.size() == subtaskDone && !subtasksLinked.isEmpty()) {
            status = "DONE";
        } else if (subtaskInProgress > 0 || subtaskDone != 0) {
            status = "IN_PROGRESS";
        } else {
            status = "NEW";
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
