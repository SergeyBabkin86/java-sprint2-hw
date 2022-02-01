public class Task {
    private Integer taskID;
    private String taskName;
    private String taskDescription;
    private String taskStatus = "NEW";

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task(Integer taskID, String taskName, String taskDescription, String taskStatus) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public Integer getTaskID() {
        return taskID;
    }

    @Override
    public String toString() {
        return "\tTask{" +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
