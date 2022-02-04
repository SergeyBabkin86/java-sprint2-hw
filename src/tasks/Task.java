package tasks;

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

    @Override
    public String toString() {
        return "\tTask{" +
                "taskId=" + id +
                ", taskName='" + name + '\'' +
                ", taskDescription='" + description + '\'' +
                ", taskStatus='" + status + '\'' +
                '}';
    }
}
