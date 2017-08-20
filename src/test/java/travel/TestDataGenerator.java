package travel;

import org.apache.commons.lang3.RandomStringUtils;
import travel.model.Location;
import travel.model.User;
import travel.model.Visit;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Random;

public class TestDataGenerator {

    public static String randomString(int size) {
        return RandomStringUtils.randomAlphanumeric(size).toUpperCase();
    }

    public static long getBirthDay() {
        Calendar calendar = Calendar.getInstance();
        Random random = new Random();
        int years = random.nextInt(70);
        int month = random.nextInt(12);
        int day = random.nextInt(30);
        calendar.add(Calendar.YEAR, -years);
        calendar.add(Calendar.MONTH, -month);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTimeInMillis() / 1000;
    }

    public static long getVisited() {
        Calendar calendar = Calendar.getInstance();
        Random random = new Random();
        int years = random.nextInt(15);
        int month = random.nextInt(12);
        int day = random.nextInt(30);
        calendar.add(Calendar.YEAR, -years);
        calendar.add(Calendar.MONTH, -month);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTimeInMillis() / 1000;
    }

    public static char getGender() {
        Random random = new Random();
        int r = random.nextInt(2);
        if (r == 0) {
            return 'm';
        } else {
            return 'f';
        }
    }

    // U - 800, L - 1000, V - 10000
    public static void main(String[] args) throws  Exception {
        int USERS = 160000;
        int LOCATIONS = 200000;
        int VISITS = 2000000;
        Random random = new Random();

        PrintWriter writer = new PrintWriter("C:\\Projects\\travel\\data\\data\\big\\users_1.json", "UTF-8");
        writer.write("{\"users\": [");
        for (long i = 0; i < USERS; i++) {
            User user = new User(i, randomString(20), randomString(20), getBirthDay(), getGender(), randomString(20));
            writer.write(user.toJson().toString());
            if (i != USERS - 1) {
                writer.write(",");
            }
        }
        writer.write("]}");

        writer.close();

        writer = new PrintWriter("C:\\Projects\\travel\\data\\data\\big\\locations_1.json", "UTF-8");
        writer.write("{\"locations\": [");
        for (long i = 0; i < LOCATIONS; i++) {
            Location location = new Location(i, (long)random.nextInt(500), randomString(50), randomString(50), randomString(50));
            writer.write(location.toJson().toString());
            if (i != LOCATIONS - 1) {
                writer.write(",");
            }
        }
        writer.write("]}");
        writer.close();

        writer = new PrintWriter("C:\\Projects\\travel\\data\\data\\big\\visits_1.json", "UTF-8");
        writer.write("{\"visits\": [");
        for (long i = 0; i < VISITS; i++) {
            Visit visit = new Visit(i, (long)random.nextInt(USERS), (long)random.nextInt(LOCATIONS), getVisited(), random.nextInt(6));
            writer.write(visit.toJson().toString());
            if (i != VISITS - 1) {
                writer.write(",");
            }
        }
        writer.write("]}");
        writer.close();
    }
}
