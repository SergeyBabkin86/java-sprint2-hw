public class SubTask {
    private int subTaskId;
    private int epicId;
    private String subTaskDescription; // Т.к. сабтаск является частью эпика --> subtask(s) name - epicName).
    private String status = "NEW"; // По условиям задачи при создании объекта статутс всегда NEW

    public SubTask(int epicId, String subTaskDescription) {
        this.epicId = epicId;
        this.subTaskDescription = subTaskDescription;
    }

    public SubTask(int subTaskId, int epicId, String subTaskDescription, String epicStatus) {
        this.subTaskId = subTaskId;
        this.epicId = epicId;
        this.subTaskDescription = subTaskDescription;
        this.status = epicStatus;
    }

    public void setSubTaskId(int subTaskId) {
        this.subTaskId = subTaskId;
    }

    public int getSubTaskId() {
        return subTaskId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return " \t\tSubTask{" +
                "subTaskId=" + subTaskId +
                ", subTaskDescription='" + subTaskDescription + '\'' +
                ", epicStatus='" + status + '\'' +
                '}';
    }
}
