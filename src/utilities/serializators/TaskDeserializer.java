package utilities.serializators;

import com.google.gson.*;
import tasks.Task;
import utilities.TaskStatus;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskDeserializer implements JsonDeserializer<Task> {

    @Override
    public Task deserialize(JsonElement json,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        var jsonObject = json.getAsJsonObject();

        var title = jsonObject.get("title").getAsString();
        var description = jsonObject.get("description").getAsString();
        var status = deserializeStatus(jsonObject.get("status").getAsString());

        var task = getTask(jsonObject, title, description, status);

        setStartTime(jsonObject, task);
        setDuration(jsonObject, task);

        return task;
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

    private Task getTask(JsonObject jsonObject, String title, String description, TaskStatus taskStatus) {
        Task task;
        if (!jsonObject.get("id").getAsString().equals("")) {
            var id = jsonObject.get("id").getAsInt();
            task = new Task(id, title, description, taskStatus);
        } else {
            task = new Task(null, title, description, taskStatus);
        }
        return task;
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
