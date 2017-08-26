package travel.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

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

    private static boolean isNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((i == 0 && c == '-') || (c >= '0' && c <= '9')) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static int getAsIntegerPrimitive(JsonObject object, String field) {
        JsonElement element = object.get(field);
        if (element != null && !element.isJsonNull()) {
            String strElem = element.getAsString();
            if (isNumeric(strElem)) {
                return element.getAsInt();
            } else {
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
