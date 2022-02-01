import java.util.ArrayList;
import java.util.HashMap;

public class Epic {
    private int epicId;
    private String epicName;
    private String epicStatus = "NEW";

    HashMap<Integer, String> subTaskReference = new HashMap<>();

    public Epic(String epicName) {
        this.epicName = epicName;
    }

    public Epic(int epicId, String epicName) {
        this.epicId = epicId;
        this.epicName = epicName;
    }

     void amendEpicStatus() { // Метод для изменения статуса эпика.
        ArrayList<String> subTaskDone = new ArrayList<>();
        ArrayList<String> subTaskInProgress = new ArrayList<>();
        for (Integer key : subTaskReference.keySet()) {
            if (subTaskReference.get(key).equals("DONE")) {
                subTaskDone.add(subTaskReference.get(key));
            } else if (subTaskReference.get(key).equals("IN_PROGRESS")) {
                subTaskInProgress.add(subTaskReference.get(key));
            }
        }
        if (subTaskReference.size() == subTaskDone.size()) {
            this.epicStatus = "DONE";
        } else if (!subTaskInProgress.isEmpty() || !subTaskDone.isEmpty()) {
            this.epicStatus = "IN_PROGRESS";
        }
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "\tEpicTask{" +
                "epicId=" + epicId +
                ", epicName='" + epicName + '\'' +
                ", epicStatus='" + epicStatus + '\'' +
                '}';
    }
}
