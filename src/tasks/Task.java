package tasks;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected String status = "NEW";

    public Task() {
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(Integer id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setId(Integer taskId) { //!!! Возможно нужно будет удалить
        this.id = taskId;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "\tTask{" +
                "taskID=" + id +
                ", taskName='" + name + '\'' +
                ", taskDescription='" + description + '\'' +
                ", taskStatus='" + status + '\'' +
                '}';
    }
}
