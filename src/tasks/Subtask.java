package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(Integer id, int epicId, String name, String description, TaskStatus status) {
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
        return id + "," + TaskType.SUBTASK + "," + name + "," + status + "," + description + "," + epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}