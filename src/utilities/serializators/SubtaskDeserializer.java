package utilities.serializators;

import com.google.gson.*;
import tasks.Subtask;
import tasks.Task;
import utilities.TaskStatus;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubtaskDeserializer implements JsonDeserializer<Subtask> {

    @Override
    public Subtask deserialize(JsonElement json,
                               Type type,
                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        var jsonObject = json.getAsJsonObject();

        var epicId = jsonObject.get("epicId").getAsInt();
        var title = jsonObject.get("title").getAsString();
        var description = jsonObject.get("description").getAsString();
        var status = deserializeStatus(jsonObject.get("status").getAsString());

        var subtask = getSubtask(jsonObject, epicId, title, description, status);

        setStartTime(jsonObject, subtask);
        setDuration(jsonObject, subtask);

        return subtask;
    }

    private TaskStatus deserializeStatus(String status) {
        TaskStatus taskStatus;
        if (status.equals("NEW")) {
            taskStatus = TaskStatus.NEW;
        } else if (status.equals("IN_PROGRESS")) {
            taskStatus = TaskStatus.IN_PROGRESS;
        } else {
            taskStatus = TaskStatus.DONE;
        }
        return taskStatus;
    }

    private Subtask getSubtask(JsonObject jsonObject,
                               int epicId,
                               String title,
                               String description,
                               TaskStatus taskStatus) {
        Subtask subtask;
        if (!jsonObject.get("id").getAsString().equals("")) {
            var id = jsonObject.get("id").getAsInt();
            subtask = new Subtask(id, epicId, title, description, taskStatus);
        } else {
            subtask = new Subtask(null, epicId, title, description, taskStatus);
        }
        return subtask;
    }

    private void setStartTime(JsonObject jsonObject, Task task) {
        var startTime = jsonObject.get("start").getAsString();
        if (!startTime.equals("")) {
            var localDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
            task.setStartTime(localDateTime);
        } else {
            task.setStartTime(null);
        }
    }

    private void setDuration(JsonObject jsonObject, Task task) {
        var duration = jsonObject.get("duration").getAsString();
        if (!duration.equals("")) {
            var durationToSet = Duration.parse(duration);
            task.setDuration(durationToSet);
        } else {
            task.setDuration(null);
        }
    }
}
