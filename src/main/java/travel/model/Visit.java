package travel.model;

import com.google.gson.JsonObject;

import java.util.Objects;

// {"user": 44, "location": 32, "visited_at": 1103485742, "id": 1, "mark": 4}
public class Visit {
    public int id;
    public int user;
    public int location;
    public int visited;
    public int mark;

    public Visit(int id, int user, int location, int visited, int mark) {
        this.id = id;
        this.user = user;
        this.location = location;
        this.visited = visited;
        this.mark = mark;
    }

    public Visit(JsonObject object) {
        this.id = JsonUtil.getAsIntegerPrimitive(object, "id");
        this.user = JsonUtil.getAsIntegerPrimitive(object, "user");
        this.location = JsonUtil.getAsIntegerPrimitive(object, "location");
        this.visited = JsonUtil.getAsIntegerPrimitive(object, "visited_at");
        this.mark = JsonUtil.getAsIntegerPrimitive(object, "mark");
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("user", user);
        object.addProperty("location", location);
        object.addProperty("visited_at", visited);
        object.addProperty("mark", mark);
        return object;
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visit visit = (Visit) o;
        return id == visit.id &&
                user == visit.user &&
                location == visit.location &&
                visited == visit.visited &&
                mark == visit.mark;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, location, visited, mark);
    }
    */
}
