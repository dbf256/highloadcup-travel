package travel;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import travel.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StorageTest {

    @Test
    public void testSaveAndGet() {
        Storage storage = new Storage();
        User user = new User(1, "a", "b", 2, 'm', "a@a.a");
        storage.insert(user);
        User user2 = storage.getUser(user.id);
        assertEquals(user, user2);

        Location location = new Location(1, 2, "q", "w", "e");
        storage.insert(location);
        Location location2 = storage.getLocation(location.id);
        assertEquals(location, location2);

        Visit visit = new Visit(1, 2, 3, 4, 5);
        storage.insert(visit);
        Visit visit2 = storage.getVisit(visit.id);
        assertEquals(visit, visit2);
    }

    @Test
    public void testUpdate() {
        Storage storage = new Storage();
        {
            User user1 = new User(1, "a", "b", 2, 'm', "a@a.a");
            storage.insert(user1);
            User user2 = new User(Constants.INT_FIELD_MISSING, "a2", "b2", 3, 'f', "a@a.a2");
            storage.update(user1.id, user2);
            User user3 = storage.getUser(user1.id);
            user2.id = user3.id;
            assertEquals(user2, user3);
        }

        {
            Location location1 = new Location(1, 2, "a", "b", "c");
            storage.insert(location1);
            Location location2 = new Location(Constants.INT_FIELD_MISSING, 3, "d", "e", "f");
            storage.update(location1.id, location2);
            Location location3 = storage.getLocation(location1.id);
            location2.id = location3.id;
            assertEquals(location2, location3);
        }

        {
            Visit visit1 = new Visit(1, 2, 3, 4, 5);
            storage.insert(visit1);
            Visit visit2 = new Visit(Constants.INT_FIELD_MISSING, 6, 7, 8, 9);
            storage.update(visit1.id, visit2);
            Visit visit3 = storage.getVisit(visit1.id);
            visit2.id = visit3.id;
            assertTrue(EqualsBuilder.reflectionEquals(visit2, visit3));
        }

    }


    @Test
    public void testGetAvg() {

        int year = 60 * 60 * 24 * 365;

        User user1 = new User(1, "a1", "b1", 1 * year, 'm', "a@a.a1");
        User user2 = new User(2, "a2", "b2", 2 * year, 'm', "a@a.a2");
        User user3 = new User(3, "a3", "b3", 3 * year, 'f', "a@a.a2");

        Location location1 = new Location(1, 1, "q1", "w1", "s1");
        Location location2 = new Location(2, 2, "q2", "w2", "s2");
        Location location3 = new Location(3, 3, "q3", "w3", "s3");

        Visit visit1 = new Visit(1, 1, 1, 10, 1);
        Visit visit2 = new Visit(2, 2, 2, 20, 2);
        Visit visit3 = new Visit(3, 3, 3, 30, 3);

        Storage storage = new Storage();

        storage.insert(user1);storage.insert(user2);storage.insert(user3);
        storage.insert(location1);storage.insert(location2);storage.insert(location3);
        storage.insert(visit1);storage.insert(visit2);storage.insert(visit3);

        assertEquals(1.0, storage.locationAverage(1, null, null, null, null, null), 0.1);

        assertEquals(0.0, storage.locationAverage(1, null, null, null, null, 'f'), 0.1);
        assertEquals(3.0, storage.locationAverage(3, null, null, null, null, 'f'), 0.1);

        assertEquals(0.0, storage.locationAverage(1, 11, null, null, null, null), 0.1);
        assertEquals(0.0, storage.locationAverage(1, null, 5, null, null, null), 0.1);
        assertEquals(1.0, storage.locationAverage(1, 5, 11, null, null, null), 0.1);

        assertEquals(1.0, storage.locationAverage(1, null, null, 10, null, null), 0.1);
        assertEquals(0.0, storage.locationAverage(1, null, null, 100, null, null), 0.1);
        assertEquals(1.0, storage.locationAverage(1, null, null, null, 100, null), 0.1);
        assertEquals(0.0, storage.locationAverage(1, null, null, null, 10, null), 0.1);
        assertEquals(1.0, storage.locationAverage(1, null, null, 10, 100, null), 0.1);
        assertEquals(0.0, storage.locationAverage(1, null, null, 100, 10, null), 0.1);

    }

    @Test
    public void testUserVisits() {
        int year = 60 * 60 * 24 * 365;

        User user1 = new User(1, "a1", "b1", 1 * year, 'm', "a@a.a1");
        Location location1 = new Location(1, 1, "q1", "w1", "s1");
        Location location2 = new Location(2, 3, "q2", "w2", "s2");
        Visit visit1 = new Visit(1, 1, 1, 10, 1);
        Visit visit2 = new Visit(2, 1, 2, 20, 2);

        Storage storage = new Storage();

        storage.insert(user1);
        storage.insert(location1);storage.insert(location2);
        storage.insert(visit1);storage.insert(visit2);

        UserVisit userVisit1 = new UserVisit(1, 10, "w1");
        UserVisit userVisit2 = new UserVisit(2, 20, "w2");

        assertEquals(Arrays.asList(visit1, visit2), storage.userVisits(1, null, null, null, null));

        assertEquals(Arrays.asList(visit1), storage.userVisits(1, null, null, null, "s1"));
        assertEquals(Arrays.asList(), storage.userVisits(1, null, null, null, "s3"));

        assertEquals(Arrays.asList(visit1), storage.userVisits(1, null, null, 2, null));
        assertEquals(Arrays.asList(), storage.userVisits(1, null, null, 0, null));

        assertEquals(Arrays.asList(visit1, visit2), storage.userVisits(1, 5, null, null, null));
        assertEquals(Arrays.asList(visit2), storage.userVisits(1, 15, null, null, null));
        assertEquals(Arrays.asList(), storage.userVisits(1, 20, null, null, null));

        assertEquals(Arrays.asList(visit1, visit2), storage.userVisits(1, null, 30, null, null));
        assertEquals(Arrays.asList(visit1), storage.userVisits(1, null, 15, null, null));
        assertEquals(Arrays.asList(), storage.userVisits(1, null, 5, null, null));


    }

    @Test
    public void testIndexes() {
        User user1 = new User(1, "a1", "b1", 1, 'm', "a@a.a1");
        User user2 = new User(2, "a2", "b2", 2,'m', "a@a.a2");
        Location location1 = new Location(1, 1, "q1", "w1", "s1");
        Location location2 = new Location(2, 3, "q2", "w2", "s2");
        Location location3 = new Location(3, 1, "q1", "w1", "s1");
        Location location4 = new Location(4, 3, "q2", "w2", "s2");
        Visit visit1 = new Visit(1, 1, 1, 10, 1);
        Visit visit2 = new Visit(2, 1, 2, 20, 2);

        /* check insert */
        Storage storage = new Storage();
        storage.insert(user1);
        storage.insert(location1);storage.insert(location2);storage.insert(location3);storage.insert(location4);
        storage.insert(visit1);storage.insert(visit2);

        assertEquals(Arrays.asList(visit1, visit2), storage.visitsByUser.get(user1.id));
        assertEquals(Arrays.asList(visit1), storage.visitsByLocation.get(location1.id));
        assertEquals(Arrays.asList(visit2), storage.visitsByLocation.get(location2.id));
        assertEquals(Arrays.asList(), storage.visitsByUser.getOrDefault(user2.id, Collections.emptyList()));
        assertEquals(Arrays.asList(), storage.visitsByLocation.getOrDefault(location3.id, Collections.emptyList()));
        assertEquals(Arrays.asList(), storage.visitsByLocation.getOrDefault(location4.id, Collections.emptyList()));

        /* change user and location */
        Visit visit1Updated = new Visit(1, 2, 3, 10, 1);
        Visit visit2Updated = new Visit(2, 2, 4, 20, 2);

        storage.update(visit1.id, visit1Updated);
        storage.update(visit2.id, visit2Updated);

        assertEquals(Arrays.asList(), storage.visitsByUser.getOrDefault(user1.id, Collections.emptyList()));
        assertEquals(Arrays.asList(), storage.visitsByLocation.getOrDefault(location1.id, Collections.emptyList()));
        assertEquals(Arrays.asList(), storage.visitsByLocation.getOrDefault(location1.id, Collections.emptyList()));
        assertEquals(Arrays.asList(visit1, visit2), storage.visitsByUser.get(user2.id));
        assertEquals(Arrays.asList(visit1), storage.visitsByLocation.get(location3.id));
        assertEquals(Arrays.asList(visit2), storage.visitsByLocation.get(location4.id));

        /* change user and location back */
        Visit visit1Updated2 = new Visit(1, 1, 1, 10, 1);
        Visit visit2Updated2 = new Visit(2, 1, 2, 20, 2);

        storage.update(visit1.id, visit1Updated2);
        storage.update(visit2.id, visit2Updated2);

        assertEquals(Arrays.asList(visit1, visit2), storage.visitsByUser.get(user1.id));
        assertEquals(Arrays.asList(visit1), storage.visitsByLocation.get(location1.id));
        assertEquals(Arrays.asList(visit2), storage.visitsByLocation.get(location2.id));
        assertEquals(Arrays.asList(), storage.visitsByUser.getOrDefault(user2.id, Collections.emptyList()));
        assertEquals(Arrays.asList(), storage.visitsByLocation.getOrDefault(location3.id, Collections.emptyList()));
        assertEquals(Arrays.asList(), storage.visitsByLocation.getOrDefault(location4.id, Collections.emptyList()));

    }

    @Test
    public void testVisitIdNpe() {
        User user1 = new User(1, "a1", "b1", 1, 'm', "a@a.a1");
        User user2 = new User(1, "a1", "b1", 1, 'm', "a@a.a1");
        Location location1 = new Location(1, 1, "q1", "w1", "s1");
        Location location2 = new Location(2, 1, "q1", "w1", "s1");
        Visit visit1 = new Visit(1, 1, 1, 10, 1);
        Storage storage = new Storage();
        storage.insert(user1);storage.insert(user2);
        storage.insert(location1);
        storage.insert(visit1);storage.insert(location2);

        Visit visit1Updated1 = new Visit(Constants.INT_FIELD_MISSING, 2, 2, 10, 1);
        storage.update(visit1.id, visit1Updated1);

        storage.locationAverage(location2.id, null, null, null, null, null);
        storage.userVisits(user2.id, null, null, null, null);

    }
}
