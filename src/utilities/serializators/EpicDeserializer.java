package utilities.serializators;

import com.google.gson.*;
import tasks.Epic;
import tasks.Subtask;
import utilities.TaskStatus;

import java.lang.reflect.Type;

public class EpicDeserializer implements JsonDeserializer<Epic> {

    @Override
    public Epic deserialize(JsonElement json,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        var jsonObject = json.getAsJsonObject();

        var title = jsonObject.get("title").getAsString();
        var description = jsonObject.get("description").getAsString();
        var status = deserializeStatus(jsonObject.get("status").getAsString());

        var epic = getEpic(jsonObject, title, description, status);

        setLinkedSubtasks(jsonDeserializationContext, jsonObject, epic);

        epic.amendEpicStatus();
        epic.setEpicTiming();

        return epic;
    }

    private void setLinkedSubtasks(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, Epic epic) {
        if (!epic.subtasksLinked.isEmpty()) {
            JsonArray subtaskLinked = jsonObject.getAsJsonArray("subtaskLinked");
            for (JsonElement subtask : subtaskLinked) {
                epic.subtasksLinked.add(jsonDeserializationContext.deserialize(subtask, Subtask.class));
            }
        }
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

    private Epic getEpic(JsonObject jsonObject,
                         String title,
                         String description,
                         TaskStatus status) {
        Epic epic;
        if (!jsonObject.get("id").getAsString().equals("")) {
            var id = jsonObject.get("id").getAsInt();
            epic = new Epic(id, title, description, status);
        } else {
            epic = new Epic(null, title, description, status);
        }
        return epic;
    }
}
