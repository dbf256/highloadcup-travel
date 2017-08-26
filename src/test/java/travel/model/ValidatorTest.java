package travel.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.fail;

public class ValidatorTest {

    private Validator validator = new Validator();

    @Test
    public void testVisitInsert() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, - 10);
        Visit visit = new Visit(1, 2, 3, (int)(cal.getTimeInMillis() / 1000), 2);
        Assert.assertTrue(validator.validate(visit, false));
    }
    
    @Test
    public void testVisitUpdate() {
        Visit visit = new Visit(JsonUtil.fromString("{mark: 5}"));
        Assert.assertTrue(validator.validate(visit, true));
    }

    @Test
    public void testUserUpdateNull() {
        User user1 = new User(JsonUtil.fromString("{\"first_name\": \"a\"}"));
        Assert.assertTrue(validator.validate(user1, true));
        try {
            User user2 = new User(JsonUtil.fromString("{\"first_name\": \"a\", \"last_name\": null}"));
            fail();
        } catch (IllegalArgumentException e) {};
    }
}
