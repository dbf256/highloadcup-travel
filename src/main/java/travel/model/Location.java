package travel.model;

import com.google.gson.JsonObject;

import java.util.Objects;

import static travel.model.JsonUtil.getAsLong;
import static travel.model.JsonUtil.getAsLongPrimitive;

// {"distance": 6, "city": "Москва", "place": "Набережная", "id": 1, "country": "Аргентина"}
public class Location {
    public long id;
    public long distance;
    public String city;
    public String place;
    public String country;

    public Location(long id, long distance, String city, String place, String country) {
        this.id = id;
        this.distance = distance;
        this.city = city;
        this.place = place;
        this.country = country;
    }

    public Location(JsonObject object) {
        this.id = getAsLongPrimitive(object, "id");
        this.distance = getAsLongPrimitive(object, "distance");
        this.city = JsonUtil.getAsString(object, "city");
        this.place = JsonUtil.getAsString(object, "place");
        this.country = JsonUtil.getAsString(object, "country");
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("distance", distance);
        object.addProperty("city", city);
        object.addProperty("place", place);
        object.addProperty("country", country);
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id) &&
                Objects.equals(distance, location.distance) &&
                Objects.equals(city, location.city) &&
                Objects.equals(place, location.place) &&
                Objects.equals(country, location.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, distance, city, place, country);
    }
}
