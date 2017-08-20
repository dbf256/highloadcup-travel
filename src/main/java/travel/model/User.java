package travel.model;

import com.google.gson.JsonObject;

import java.util.Objects;

import static travel.model.JsonUtil.*;

// {"first_name": "Пётр", "last_name": "Фетатосян", "birth_date": -1720915200, "gender": "m", "id": 1, "email": "wibylcudestiwuk@icloud.com"}
public class User {
    public long id;
    public String firstName;
    public String lastName;
    public long birthDate;
    public Character gender;
    public String email;

    public User(long id, String firstName, String lastName, long birthDate, Character gender, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
    }

    public User(JsonObject object) {
        this.id = getAsLongPrimitive(object, "id");
        this.firstName = getAsString(object, "first_name");
        this.lastName = JsonUtil.getAsString(object, "last_name");
        this.birthDate = JsonUtil.getAsLongPrimitive(object, "birth_date");
        this.gender = JsonUtil.getAsCharacter(object, "gender");
        this.email = JsonUtil.getAsString(object, "email");
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("first_name", firstName);
        object.addProperty("last_name", lastName);
        object.addProperty("birth_date", birthDate);
        object.addProperty("gender", gender);
        object.addProperty("email", email);
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                birthDate == user.birthDate &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, birthDate, gender, email);
    }
}
