package tasks;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected String status;

    public Task(Integer id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setId(Integer taskId) {
        this.id = taskId;
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "\tTask{" +
                "taskId=" + id +
                ", taskName='" + name + '\'' +
                ", taskDescription='" + description.length() + '\'' +
                ", taskStatus='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
