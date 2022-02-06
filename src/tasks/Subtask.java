package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(Integer id, int epicId, String name, String description, String status) {
        super(id, name, description, status);
        this.id = id;
        this.epicId = epicId;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return " \tSubtask{" +
                "subtaskId=" + id +
                ", epicId='" + epicId + '\'' +
                ", subtaskName='" + name + '\'' +
                ", subtaskDescription='" + description.length() + '\'' +
                ", subtaskStatus='" + status + '\'' +
                '}';
    }
}
