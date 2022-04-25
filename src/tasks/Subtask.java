package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private final TaskType taskType = TaskType.SUBTASK;
    private final int epicId;

    public Subtask(int epicId, String title, String description, TaskStatus status) {
        super(title, description, status);
        this.epicId = epicId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Subtask(Integer id, int epicId, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        this.id = id;
        this.epicId = epicId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return id + "," +
                taskType + "," +
                title + "," +
                status + "," +
                description + "," +
                epicId + "," +
                startTime + "," +
                duration + "," +
                getEndTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(id, subtask.id) &&
                Objects.equals(taskType, subtask.taskType) &&
                Objects.equals(title, subtask.title) &&
                Objects.equals(description, subtask.description) &&
                status == subtask.status &&
                Objects.equals(startTime, subtask.startTime) &&
                Objects.equals(duration, subtask.duration) &&
                epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}