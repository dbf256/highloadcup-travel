package travel.model;

import com.google.gson.JsonParser;
import org.junit.Assert;

public class TestUtil {
    public static void assertJsonStringEquals(String first, String second) {
        JsonParser parser = new JsonParser();
        Assert.assertEquals(parser.parse(first).getAsJsonObject(), parser.parse(second));
    }
}
