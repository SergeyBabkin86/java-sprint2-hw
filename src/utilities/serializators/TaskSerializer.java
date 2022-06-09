package utilities.serializators;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import tasks.Task;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class TaskSerializer implements JsonSerializer<Task> {

    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject result = new JsonObject();

        serializeId(task, result);

        result.addProperty("type", "TASK");
        result.addProperty("title", task.getTitle());
        result.addProperty("description", task.getDescription());
        result.addProperty("status", String.valueOf(task.getStatus()));

        serializeStartTime(task, result);
        serializeDuration(task, result);
        serializeEndTime(task, result);

        return result;
    }

    private void serializeId(Task task, JsonObject result) {
        if (task.getId() != null) {
            result.addProperty("id", task.getId());
        } else {
            result.addProperty("id", "");
        }
    }

    private void serializeStartTime(Task task, JsonObject result) {
        if (task.getStartTime() != null) {
            result.addProperty("start", task.getStartTime().
                    format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        } else {
            result.addProperty("start", "");
        }
    }

    private void serializeDuration(Task task, JsonObject result) {
        if (task.getDuration() != null) {
            result.addProperty("duration", String.valueOf(task.getDuration()));
        } else {
            result.addProperty("duration", "");
        }
    }

    private void serializeEndTime(Task task, JsonObject result) {
        if (task.getDuration() != null && task.getStartTime() != null) {
            result.addProperty("end", task.getEndTime().
                    format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        } else {
            result.addProperty("end", "");
        }
    }
}
