package tasks;

import utilities.TaskStatus;
import utilities.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected final TaskType taskType = TaskType.EPIC;
    protected LocalDateTime endTime;

    public final List<Subtask> subtasksLinked = new ArrayList<>();

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Epic(Integer id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public void setEpicTiming() {
        if (!subtasksLinked.isEmpty()) {
            for (Subtask subtask : subtasksLinked) {
                if (startTime == null) {
                    startTime = subtask.getStartTime();
                } else {
                    if (subtask.getStartTime() != null && startTime.isAfter(subtask.getStartTime())) {
                        startTime = subtask.getStartTime();
                    }
                }
                if (endTime == null) {
                    endTime = subtask.getEndTime();
                } else {
                    if (subtask.getEndTime() != null && endTime.isBefore(subtask.getEndTime())) {
                        endTime = subtask.getEndTime();
                    }
                }
            }
            if (endTime != null && startTime != null) {
                duration = Duration.between(getStartTime(), getEndTime());
            }
        } else {
            startTime = null;
            duration = null;
            endTime = null;
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
        return id + "," +
                taskType + "," +
                title + "," +
                status + "," +
                description + "," +
                "" + "," +
                startTime + "," +
                duration + "," +
                getEndTime();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
