package travel.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtil {
    public static String getAsString(JsonObject object, String field) {
        JsonElement element = object.get(field);
        if (element != null && !element.isJsonNull()) {
            return element.getAsString();
        } else if (element != null && element.isJsonNull()) {
            throw new IllegalArgumentException();
        } else {
            return null;
        }
    }

    public static Character getAsCharacter(JsonObject object, String field) {
        JsonElement element = object.get(field);
        if (element != null && !element.isJsonNull()) {
            return element.getAsCharacter();
        } else if (element != null && element.isJsonNull()) {
            throw new IllegalArgumentException();
        } else {
            return null;
        }
    }

    public static int getAsIntegerPrimitive(JsonObject object, String field) {
        JsonElement element = object.get(field);
        if (element != null && !element.isJsonNull()) {
            try {
                return element.getAsInt();
            } catch (ClassCastException e) {
                throw new IllegalArgumentException();
            }
        } else if (element != null && element.isJsonNull()) {
            throw new IllegalArgumentException();
        } else {
            return Constants.INT_FIELD_MISSING;
        }
    }

    public static JsonObject fromString(String source) {
        JsonParser parser = new JsonParser();
        return parser.parse(source).getAsJsonObject();
    }
    
}
