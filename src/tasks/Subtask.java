package tasks;

public class Subtask extends Task {
    private int epicId;

    // Конструкторы ниже не учитывают имя сабтаска,т.к.он - часть эпика и его название - названия эпика.
    public Subtask(int epicId, String description) {
        this.epicId = epicId;
        this.description = description;
    }

    public Subtask(int id, int epicId, String description, String status) {
        this.id = id;
        this.epicId = epicId;
        this.description = description;
        this.status = status;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return " \t\tSubTask{" +
                "subTaskId=" + id +
                ", subTaskDescription='" + description + '\'' +
                ", epicStatus='" + status + '\'' +
                '}';
    }
}
