package travel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import travel.model.Location;
import travel.model.User;
import travel.model.Visit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static travel.model.Constants.BUF_SIZE;
import static travel.model.Constants.INT_FIELD_MISSING;

class Storage {
    public final Map<Integer, User> users = new ConcurrentHashMap<>();
    public final Map<Integer, Location> locations = new ConcurrentHashMap<>();
    public final Map<Integer, Visit> visits = new ConcurrentHashMap<>();
    public final Map<Integer, List<Visit>> visitsByUser = new ConcurrentHashMap<>();
    public final Map<Integer, List<Visit>> visitsByLocation = new ConcurrentHashMap<>();

    public final Map<Long, ByteBuf> userJson = new ConcurrentHashMap<>();
    public final Map<Long, ByteBuf> locationJson = new ConcurrentHashMap<>();
    public final Map<Long, ByteBuf> visitJson = new ConcurrentHashMap<>();


    public final AtomicLong currentTime = new AtomicLong(System.currentTimeMillis());

    public void clear() {
        users.clear();
        locations.clear();
        visits.clear();
        visitsByUser.clear();
        visitsByLocation.clear();
    }

    public void updateUserJson(Long id, String json) {
        ByteBuf buf = Unpooled.buffer(BUF_SIZE);
        buf.writeBytes(json.getBytes(CharsetUtil.UTF_8));
        userJson.put(id, buf);
    }

    public void updateLocationJson(Long id, String json) {
        ByteBuf buf = Unpooled.buffer(BUF_SIZE);
        buf.writeBytes(json.getBytes(CharsetUtil.UTF_8));
        locationJson.put(id, buf);
    }

    public void updateVisitJson(Long id, String json) {
        ByteBuf buf = Unpooled.buffer(BUF_SIZE);
        buf.writeBytes(json.getBytes(CharsetUtil.UTF_8));
        visitJson.put(id, buf);
    }

    public ByteBuf getUserJson(Long id) {
        return userJson.get(id);
    }

    public ByteBuf getVisitJson(Long id) {
        return visitJson.get(id);
    }

    public ByteBuf getLocationJson(Long id) {
        return locationJson.get(id);
    }


    public void insert(User user) {
        users.put(user.id, user);
    }

    public void update(int id, User update) {
        User currentUser = users.get(id);
        if (currentUser == null) {
            throw new StorageNotFoundException();
        }
        if (update.birthDate != INT_FIELD_MISSING) {
            currentUser.birthDate = update.birthDate;
        }
        if (update.firstName != null) {
            currentUser.firstName = update.firstName;
        }
        if (update.gender != null) {
            currentUser.gender = update.gender;
        }
        if (update.lastName != null) {
            currentUser.lastName = update.lastName;
        }
        if (update.email != null) {
            currentUser.email = update.email;
        }
        //ByteBuf buf = Unpooled.buffer(BUF_SIZE);
        //buf.writeBytes(currentUser.toJson().toString().getBytes(CharsetUtil.UTF_8));
        //userJson.put(id, buf);
    }

    public void insert(Location location) {
        locations.put(location.id, location);
    }

    public void update(int id, Location update) {
        Location currentLocation = locations.get(id);
        if (currentLocation == null) {
            throw new StorageNotFoundException();
        }
        if (update.city != null) {
            currentLocation.city = update.city;
        }
        if (update.country != null) {
            currentLocation.country = update.country;
        }
        if (update.distance != INT_FIELD_MISSING) {
            currentLocation.distance = update.distance;
        }
        if (update.place != null) {
            currentLocation.place = update.place;
        }
        //ByteBuf buf = Unpooled.buffer(BUF_SIZE);
        //buf.writeBytes(currentLocation.toJson().toString().getBytes(CharsetUtil.UTF_8));
        //locationJson.put(id, buf);
    }

    public void insert(Visit visit) {
        visits.put(visit.id, visit);

        addUserVisit(visit.id, visit.user);
        addLocationVisit(visit.id, visit.location);

    }

    private void addLocationVisit(int visitId, int locationId) {
        Visit visit = visits.get(visitId);
        List<Visit> byLocation = visitsByLocation.get(locationId);
        if (byLocation == null) {
            byLocation = new ArrayList<>();
            visitsByLocation.put(locationId, byLocation);
        }
        byLocation.add(visit);
    }

    private void addUserVisit(int visitId, int userId) {
        Visit visit = visits.get(visitId);
        List<Visit> byUser = visitsByUser.get(userId);
        if (byUser == null) {
            byUser = new ArrayList<>();
            visitsByUser.put(userId, byUser);
        }
        byUser.add(visit);
    }

    private void deleteLocationVisit(int visitId, int locationId) {
        Visit visit = visits.get(visitId);
        List<Visit> byLocation = visitsByLocation.get(locationId);
        if (byLocation != null) {
            byLocation.remove(visit);
        }
    }

    private void deleteUserVisit(int visitId, int userId) {
        Visit visit = visits.get(visitId);
        List<Visit> byUser = visitsByUser.get(userId);
        if (byUser != null) {
            byUser.remove(visit);
        }
    }

    public void update(int id, Visit update) {
        Visit currentVisit = visits.get(id);
        if (currentVisit == null) {
            throw new StorageNotFoundException();
        }
        if (update.location != INT_FIELD_MISSING) {
            if (currentVisit.location != update.location) {
                deleteLocationVisit(currentVisit.id, currentVisit.location);
                addLocationVisit(currentVisit.id, update.location);
            }
            currentVisit.location = update.location;
        }
        if (update.mark != INT_FIELD_MISSING) {
            currentVisit.mark = update.mark;
        }
        if (update.user != INT_FIELD_MISSING) {
            if (currentVisit.user != update.user) {
                deleteUserVisit(currentVisit.id, currentVisit.user);
                addUserVisit(currentVisit.id, update.user);
            }
            currentVisit.user = update.user;
        }
        if (update.visited != INT_FIELD_MISSING) {
            currentVisit.visited = update.visited;
        }
        //ByteBuf buf = Unpooled.buffer(BUF_SIZE);
        //buf.writeBytes(currentVisit.toJson().toString().getBytes(CharsetUtil.UTF_8));
        //visitJson.put(id, buf);
    }

    public User getUser(int id) {
        User currentUser = users.get(id);
        if (currentUser == null) {
            throw new StorageNotFoundException();
        }
        return currentUser;
    }

    public Location getLocation(int id) {
        Location currentLocation = locations.get(id);
        if (currentLocation == null) {
            throw new StorageNotFoundException();
        }
        return currentLocation;
    }

    public Visit getVisit(int id) {
        Visit currentVisit = visits.get(id);
        if (currentVisit == null) {
            throw new StorageNotFoundException();
        }
        return currentVisit;
    }

    public Double locationAverage(int locationId, Integer fromDate, Integer toDate, Integer fromAge, Integer toAge, Character gender) {

        long fromTimestamp = getYearsBackTimestamp(fromAge);

        long toTimestamp = getYearsBackTimestamp(toAge);

        long sum = 0;
        double num = 0;
        for (Visit visit : visitsByLocation.getOrDefault(locationId, Collections.emptyList())) {
            //Visit visit = visits.get(visitId);
            if (fromDate != null && visit.visited <= fromDate) {
                continue;
            }
            if (toDate != null && visit.visited >= toDate) {
                continue;
            }
            User visitor = users.get(visit.user);
            if (gender != null && (gender != visitor.gender)) {
                continue;
            }
            if (fromAge != null) {
                if (visitor.birthDate >= fromTimestamp) {
                    continue;
                }
            }
            if (toAge != null) {
                if (visitor.birthDate <= toTimestamp) {
                    continue;
                }
            }
            sum += visit.mark;
            num += 1;
        }
        if (num == 0) {
            return 0.0;
        } else {
            return sum / num;
        }
    }

    private long getYearsBackTimestamp(Integer fromAge) {
        long fromTimestamp = 0;
        if (fromAge != null) {
            Date date = new Date(currentTime.get());
            date.setYear(date.getYear() - fromAge);
            //Calendar fromCalendar = Calendar.getInstance();
            //fromCalendar.setTimeInMillis(currentTime.get());
            //fromCalendar.add(Calendar.YEAR, -fromAge);
            fromTimestamp = date.getTime() / 1000;
        }
        return fromTimestamp;
    }

    public List<Visit> userVisits(int userId, Integer fromDate, Integer toDate, Integer toDistance, String country) {
        List<Visit> userVisits = new ArrayList<>();
        for (Visit visit : visitsByUser.getOrDefault(userId, Collections.emptyList())) {
            //Visit visit = visits.get(visitId);
            if (toDate != null && visit.visited >= toDate) {
                continue;
            }
            if (fromDate != null && visit.visited <= fromDate) {
                continue;
            }
            Location location = locations.get(visit.location);
            if (country != null) {
                if (!country.equals(location.country)) {
                    continue;
                }
            }
            if (toDistance != null) {
                if (location.distance >= toDistance) {
                    continue;
                }
            }
            userVisits.add(visit);
        }
        userVisits.sort((v1, v2) -> v1.visited > v2.visited ? 1 : -1);
        return userVisits;
    }

    public Storage() {
    }
}
