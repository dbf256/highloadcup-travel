package travel.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.junit.Assert.fail;

public class UserTest {

    @Test
    public void fromToJson() {
        String source = "{\"first_name\": \"Пётр\", \"last_name\": \"Фетатосян\", \"birth_date\": -1720915200, \"gender\": \"m\", \"id\": 1, \"email\": \"wibylcudestiwuk@icloud.com\"}";
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(source).getAsJsonObject();
        User user = new User(json);
        String destination = user.toJson().toString();
        TestUtil.assertJsonStringEquals(source, destination);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromToJsonNull() {
        String source = "{\"first_name\": null, \"last_name\": \"Фетатосян\", \"birth_date\": -1720915200, \"gender\": \"m\", \"id\": 1, \"email\": \"wibylcudestiwuk@icloud.com\"}";
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(source).getAsJsonObject();
        User user = new User(json);
        fail();
    }
}
