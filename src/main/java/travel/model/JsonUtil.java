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

    public static Long getAsLong(JsonObject object, String field) {
        JsonElement element = object.get(field);
        if (element != null && !element.isJsonNull()) {
            String strElem = element.getAsString();
            if (StringUtils.isNumeric(strElem) || (strElem.charAt(0) == '-' && StringUtils.isNumeric(strElem.substring(1)))) {
                return element.getAsLong();
            } else {
                throw new IllegalArgumentException();
            }
        } else if (element != null && element.isJsonNull()) {
            throw new IllegalArgumentException();
        } else {
            return null;
        }
    }

    public static long getAsLongPrimitive(JsonObject object, String field) {
        JsonElement element = object.get(field);
        if (element != null && !element.isJsonNull()) {
            String strElem = element.getAsString();
            if (StringUtils.isNumeric(strElem) || (strElem.charAt(0) == '-' && StringUtils.isNumeric(strElem.substring(1)))) {
                return element.getAsLong();
            } else {
                throw new IllegalArgumentException();
            }
        } else if (element != null && element.isJsonNull()) {
            throw new IllegalArgumentException();
        } else {
            return Constants.LONG_FIELD_MISSING;
        }
    }

    public static int getAsIntegerPrimitive(JsonObject object, String field) {
        JsonElement element = object.get(field);
        if (element != null && !element.isJsonNull()) {
            String strElem = element.getAsString();
            if (StringUtils.isNumeric(strElem) || (strElem.charAt(0) == '-' && StringUtils.isNumeric(strElem.substring(1)))) {
                return (int)element.getAsLong();
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
