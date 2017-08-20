package travel.model;

import com.google.gson.JsonObject;

import java.util.Objects;

public class UserVisit {
    public int mark;
    public long visited;
    public String place;

    public UserVisit(int mark, long visited, String place) {
        this.mark = mark;
        this.visited = visited;
        this.place = place;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("mark", mark);
        object.addProperty("visited_at", visited);
        object.addProperty("place", place);
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVisit userVisit = (UserVisit) o;
        return mark == userVisit.mark &&
                visited == userVisit.visited &&
                Objects.equals(place, userVisit.place);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mark, visited, place);
    }
}
