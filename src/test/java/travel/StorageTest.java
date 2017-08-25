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
        User user = new User(1L, "a", "b", 2L, 'm', "a@a.a");
        storage.insert(user);
        User user2 = storage.getUser(user.id);
        assertEquals(user, user2);

        Location location = new Location(1L, 2L, "q", "w", "e");
        storage.insert(location);
        Location location2 = storage.getLocation(location.id);
        assertEquals(location, location2);

        Visit visit = new Visit(1L, 2L, 3L, 4L, 5);
        storage.insert(visit);
        Visit visit2 = storage.getVisit(visit.id);
        assertEquals(visit, visit2);
    }

    @Test
    public void testUpdate() {
        Storage storage = new Storage();
        {
            User user1 = new User(1L, "a", "b", 2L, 'm', "a@a.a");
            storage.insert(user1);
            User user2 = new User(Constants.LONG_FIELD_MISSING, "a2", "b2", 3L, 'f', "a@a.a2");
            storage.update(user1.id, user2);
            User user3 = storage.getUser(user1.id);
            user2.id = user3.id;
            assertEquals(user2, user3);
        }

        {
            Location location1 = new Location(1L, 2L, "a", "b", "c");
            storage.insert(location1);
            Location location2 = new Location(Constants.LONG_FIELD_MISSING, 3L, "d", "e", "f");
            storage.update(location1.id, location2);
            Location location3 = storage.getLocation(location1.id);
            location2.id = location3.id;
            assertEquals(location2, location3);
        }

        {
            Visit visit1 = new Visit(1L, 2L, 3L, 4L, 5);
            storage.insert(visit1);
            Visit visit2 = new Visit(Constants.LONG_FIELD_MISSING, 6L, 7L, 8L, 9);
            storage.update(visit1.id, visit2);
            Visit visit3 = storage.getVisit(visit1.id);
            visit2.id = visit3.id;
            assertTrue(EqualsBuilder.reflectionEquals(visit2, visit3));
        }

    }


    @Test
    public void testGetAvg() {

        long year = 60 * 60 * 24 * 365;

        User user1 = new User(1L, "a1", "b1", 1 * year, 'm', "a@a.a1");
        User user2 = new User(2L, "a2", "b2", 2 * year, 'm', "a@a.a2");
        User user3 = new User(3L, "a3", "b3", 3 * year, 'f', "a@a.a2");

        Location location1 = new Location(1L, 1L, "q1", "w1", "s1");
        Location location2 = new Location(2L, 2L, "q2", "w2", "s2");
        Location location3 = new Location(3L, 3L, "q3", "w3", "s3");

        Visit visit1 = new Visit(1L, 1L, 1L, 10L, 1);
        Visit visit2 = new Visit(2L, 2L, 2L, 20L, 2);
        Visit visit3 = new Visit(3L, 3L, 3L, 30L, 3);

        Storage storage = new Storage();

        storage.insert(user1);storage.insert(user2);storage.insert(user3);
        storage.insert(location1);storage.insert(location2);storage.insert(location3);
        storage.insert(visit1);storage.insert(visit2);storage.insert(visit3);

        assertEquals(1.0, storage.locationAverage(1L, null, null, null, null, null), 0.1);

        assertEquals(0.0, storage.locationAverage(1L, null, null, null, null, 'f'), 0.1);
        assertEquals(3.0, storage.locationAverage(3L, null, null, null, null, 'f'), 0.1);

        assertEquals(0.0, storage.locationAverage(1L, 11L, null, null, null, null), 0.1);
        assertEquals(0.0, storage.locationAverage(1L, null, 5L, null, null, null), 0.1);
        assertEquals(1.0, storage.locationAverage(1L, 5L, 11L, null, null, null), 0.1);

        assertEquals(1.0, storage.locationAverage(1L, null, null, 10L, null, null), 0.1);
        assertEquals(0.0, storage.locationAverage(1L, null, null, 100L, null, null), 0.1);
        assertEquals(1.0, storage.locationAverage(1L, null, null, null, 100L, null), 0.1);
        assertEquals(0.0, storage.locationAverage(1L, null, null, null, 10L, null), 0.1);
        assertEquals(1.0, storage.locationAverage(1L, null, null, 10L, 100L, null), 0.1);
        assertEquals(0.0, storage.locationAverage(1L, null, null, 100L, 10L, null), 0.1);

    }

    @Test
    public void testUserVisits() {
        long year = 60 * 60 * 24 * 365;

        User user1 = new User(1L, "a1", "b1", 1 * year, 'm', "a@a.a1");
        Location location1 = new Location(1L, 1L, "q1", "w1", "s1");
        Location location2 = new Location(2L, 3L, "q2", "w2", "s2");
        Visit visit1 = new Visit(1L, 1L, 1L, 10L, 1);
        Visit visit2 = new Visit(2L, 1L, 2L, 20L, 2);

        Storage storage = new Storage();

        storage.insert(user1);
        storage.insert(location1);storage.insert(location2);
        storage.insert(visit1);storage.insert(visit2);

        UserVisit userVisit1 = new UserVisit(1, 10L, "w1");
        UserVisit userVisit2 = new UserVisit(2, 20L, "w2");

        assertEquals(Arrays.asList(visit1, visit2), storage.userVisits(1L, null, null, null, null));

        assertEquals(Arrays.asList(visit1), storage.userVisits(1L, null, null, null, "s1"));
        assertEquals(Arrays.asList(), storage.userVisits(1L, null, null, null, "s3"));

        assertEquals(Arrays.asList(visit1), storage.userVisits(1L, null, null, 2L, null));
        assertEquals(Arrays.asList(), storage.userVisits(1L, null, null, 0L, null));

        assertEquals(Arrays.asList(visit1, visit2), storage.userVisits(1L, 5L, null, null, null));
        assertEquals(Arrays.asList(visit2), storage.userVisits(1L, 15L, null, null, null));
        assertEquals(Arrays.asList(), storage.userVisits(1L, 20L, null, null, null));

        assertEquals(Arrays.asList(visit1, visit2), storage.userVisits(1L, null, 30L, null, null));
        assertEquals(Arrays.asList(visit1), storage.userVisits(1L, null, 15L, null, null));
        assertEquals(Arrays.asList(), storage.userVisits(1L, null, 5L, null, null));


    }

    @Test
    public void testIndexes() {
        User user1 = new User(1L, "a1", "b1", 1L , 'm', "a@a.a1");
        User user2 = new User(2L, "a2", "b2", 2L , 'm', "a@a.a2");
        Location location1 = new Location(1L, 1L, "q1", "w1", "s1");
        Location location2 = new Location(2L, 3L, "q2", "w2", "s2");
        Location location3 = new Location(3L, 1L, "q1", "w1", "s1");
        Location location4 = new Location(4L, 3L, "q2", "w2", "s2");
        Visit visit1 = new Visit(1L, 1L, 1L, 10L, 1);
        Visit visit2 = new Visit(2L, 1L, 2L, 20L, 2);

        /* check insert */
        Storage storage = new Storage();
        storage.insert(user1);
        storage.insert(location1);storage.insert(location2);storage.insert(location3);storage.insert(location4);
        storage.insert(visit1);storage.insert(visit2);

        assertEquals(new HashSet<>(Arrays.asList(visit1, visit2)), storage.visitsByUser.get(user1.id));
        assertEquals(new HashSet<>(Arrays.asList(visit1)), storage.visitsByLocation.get(location1.id));
        assertEquals(new HashSet<>(Arrays.asList(visit2)), storage.visitsByLocation.get(location2.id));
        assertEquals(new HashSet<>(Arrays.asList()), storage.visitsByUser.getOrDefault(user2.id, Collections.emptySet()));
        assertEquals(new HashSet<>(Arrays.asList()), storage.visitsByLocation.getOrDefault(location3.id, Collections.emptySet()));
        assertEquals(new HashSet<>(Arrays.asList()), storage.visitsByLocation.getOrDefault(location4.id, Collections.emptySet()));

        /* change user and location */
        Visit visit1Updated = new Visit(1L, 2L, 3L, 10L, 1);
        Visit visit2Updated = new Visit(2L, 2L, 4L, 20L, 2);

        storage.update(visit1.id, visit1Updated);
        storage.update(visit2.id, visit2Updated);

        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList()), storage.visitsByUser.getOrDefault(user1.id, Collections.emptySet())));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList()), storage.visitsByLocation.getOrDefault(location1.id, Collections.emptySet())));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList()), storage.visitsByLocation.getOrDefault(location1.id, Collections.emptySet())));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList(visit1, visit2)), storage.visitsByUser.get(user2.id)));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList(visit1)), storage.visitsByLocation.get(location3.id)));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList(visit2)), storage.visitsByLocation.get(location4.id)));

        /* change user and location back */
        Visit visit1Updated2 = new Visit(1L, 1L, 1L, 10L, 1);
        Visit visit2Updated2 = new Visit(2L, 1L, 2L, 20L, 2);

        storage.update(visit1.id, visit1Updated2);
        storage.update(visit2.id, visit2Updated2);

        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList(visit1, visit2)), storage.visitsByUser.get(user1.id)));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList(visit1)), storage.visitsByLocation.get(location1.id)));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList(visit2)), storage.visitsByLocation.get(location2.id)));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList()), storage.visitsByUser.getOrDefault(user2.id, Collections.emptySet())));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList()), storage.visitsByLocation.getOrDefault(location3.id, Collections.emptySet())));
        assertTrue(EqualsBuilder.reflectionEquals(new HashSet<>(Arrays.asList()), storage.visitsByLocation.getOrDefault(location4.id, Collections.emptySet())));

    }

    @Test
    public void testVisitIdNpe() {
        User user1 = new User(1L, "a1", "b1", 1L , 'm', "a@a.a1");
        User user2 = new User(1L, "a1", "b1", 1L , 'm', "a@a.a1");
        Location location1 = new Location(1L, 1L, "q1", "w1", "s1");
        Location location2 = new Location(2L, 1L, "q1", "w1", "s1");
        Visit visit1 = new Visit(1L, 1L, 1L, 10L, 1);
        Storage storage = new Storage();
        storage.insert(user1);storage.insert(user2);
        storage.insert(location1);
        storage.insert(visit1);storage.insert(location2);

        Visit visit1Updated1 = new Visit(Constants.LONG_FIELD_MISSING, 2L, 2L, 10L, 1);
        storage.update(visit1.id, visit1Updated1);

        storage.locationAverage(location2.id, null, null, null, null, null);
        storage.userVisits(user2.id, null, null, null, null);

    }
}
