package utilities.serializators;

import com.google.gson.*;
import tasks.Epic;
import tasks.Subtask;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class EpicSerializer implements JsonSerializer<Epic> {

    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        serializeId(epic, result);

        result.addProperty("type", "EPIC");
        result.addProperty("title", epic.getTitle());
        result.addProperty("description", epic.getDescription());
        result.addProperty("status", String.valueOf(epic.getStatus()));

        serializeLinkedSubtasks(epic, jsonSerializationContext, result);

        serializeStartTime(epic, result);
        serializeDuration(epic, result);
        serializeEndTime(epic, result);

        return result;
    }

    private void serializeId(Epic epic, JsonObject result) {
        if (epic.getId() != null) {
            result.addProperty("id", epic.getId());
        } else {
            result.addProperty("id", "");
        }
    }

    private void serializeLinkedSubtasks(Epic epic, JsonSerializationContext jsonSerializationContext, JsonObject result) {
        if (!epic.subtasksLinked.isEmpty()) {
            JsonArray links = new JsonArray();
            result.add("subtaskLinked", links);
            for (Subtask subtask : epic.subtasksLinked) {
                links.add(jsonSerializationContext.serialize(subtask, Subtask.class));
            }
        } else {
            result.addProperty("subtaskLinked", "no_links");
        }
    }

    private void serializeStartTime(Epic epic, JsonObject result) {
        if (epic.getStartTime() != null) {
            result.addProperty("start", epic.getStartTime().
                    format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        } else {
            result.addProperty("start", "");
        }
    }

    private void serializeDuration(Epic epic, JsonObject result) {
        if (epic.getDuration() != null) {
            result.addProperty("duration", String.valueOf(epic.getDuration()));

        } else {
            result.addProperty("duration", "");
        }
    }

    private void serializeEndTime(Epic epic, JsonObject result) {
        if (epic.getDuration() != null && epic.getStartTime() != null) {
            result.addProperty("end", epic.getEndTime().
                    format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));

        } else {
            result.addProperty("end", "");
        }
    }
}
