package backend.backend.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean verified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("user_id", id)
                .add("first_name", firstName)
                .add("last_name", lastName)
                .add("verified", verified)
                .build();
    }

    public static User toUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getString("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setVerified(rs.getBoolean("verified"));
        return user;
    }

    public static User toUser(JsonObject data) {
        User user = new User();
        user.setFirstName(data.getString("first_name"));
        user.setLastName(data.getString("last_name"));
        return user;
    }
}
