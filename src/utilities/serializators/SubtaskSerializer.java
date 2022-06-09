package utilities.serializators;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import tasks.Subtask;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class SubtaskSerializer implements JsonSerializer<Subtask> {

    @Override
    public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        serializeId(subtask, result);

        result.addProperty("type", "SUBTASK");
        result.addProperty("title", subtask.getTitle());
        result.addProperty("description", subtask.getDescription());
        result.addProperty("status", String.valueOf(subtask.getStatus()));

        serializeStartTime(subtask, result);
        serializeDuration(subtask, result);
        serializeEndTime(subtask, result);

        result.addProperty("epicId", subtask.getEpicId());

        return result;
    }

    private void serializeId(Subtask subtask, JsonObject result) {
        if (subtask.getId() != null) {
            result.addProperty("id", subtask.getId());
        } else {
            result.addProperty("id", "");
        }
    }

    private void serializeStartTime(Subtask subtask, JsonObject result) {
        if (subtask.getStartTime() != null) {
            result.addProperty("start", subtask.getStartTime().
                    format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        } else {
            result.addProperty("start", "");
        }
    }

    private void serializeDuration(Subtask subtask, JsonObject result) {
        if (subtask.getDuration() != null) {
            result.addProperty("duration", String.valueOf(subtask.getDuration()));
        } else {
            result.addProperty("duration", "");
        }
    }

    private void serializeEndTime(Subtask subtask, JsonObject result) {
        if (subtask.getDuration() != null && subtask.getStartTime() != null) {
            result.addProperty("end", subtask.getEndTime().
                    format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        } else {
            result.addProperty("end", "");
        }
    }
}
