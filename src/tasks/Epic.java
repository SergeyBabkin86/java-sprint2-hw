package tasks;

import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Subtask> subtasksLinked = new ArrayList<>();

    /* Конструкторы ниже не учитывают описание эпика ,т.к. в него всегда включены сабтаски, которые и
    являются описанием (без сабтаска эпик не имеет смысла). */
    public Epic(String name) {
        this.name = name;
    }

    public Epic(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void amendEpicStatus() { // Метод для изменения статуса эпика.
        int subTaskDoneNew = 0;
        int subTaskInProgressNew = 0;
        for (Subtask subtask : subtasksLinked) {
            if (subtask.status.equals("DONE")) {
                subTaskDoneNew++;
            } else if (subtask.status.equals("IN_PROGRESS")) {
                subTaskInProgressNew++;
            }
        }
        if (subtasksLinked.size() == subTaskDoneNew) {
            status = "DONE";
        } else if (subTaskInProgressNew > 0 || subTaskDoneNew != 0) {
            status = "IN_PROGRESS";
        }
    }

    @Override
    public String toString() {
        return "\tEpicTask{" +
                "epicId=" + id +
                ", epicName='" + name + '\'' +
                ", epicStatus='" + status + '\'' +
                '}';
    }
}
