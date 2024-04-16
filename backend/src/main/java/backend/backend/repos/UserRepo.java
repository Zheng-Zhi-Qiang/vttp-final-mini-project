package backend.backend.repos;

import backend.backend.exceptions.SQLInsertionError;
import backend.backend.exceptions.SQLUpdateError;
import backend.backend.models.User;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import static backend.backend.repos.Queries.*;

@Repository
public class UserRepo {

    @Autowired
    private JdbcTemplate template;

    public Optional<User> getUserById(String id) {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_USER_BY_USER_ID, id);
        if (!rs.next()) {
            return Optional.empty();
        }
        return Optional.of(User.toUser(rs));
    }

    public void insertUser(User user) throws SQLInsertionError {
        int result = template.update(SQL_INSERT_USER, user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName());
        if (result < 1) {
            throw new SQLInsertionError("Failed to create user");
        }
    }

    public void updateUserPushMessagingToken(String userId, String token) throws SQLUpdateError {
        int result = template.update(SQL_UPDATE_USER_PUSH_MESSAGING_TOKEN, token, userId);
        if (result < 1) {
            throw new SQLUpdateError("User does not exists");
        }
    }

    public Optional<String> getUserPushMessagingToken(String userId) {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_USER_PUSH_MESSAGING_TOKEN, userId);
        if (!rs.next()) {
            return Optional.empty();
        }
        return Optional.ofNullable(rs.getString("push_messaging_token"));
    }
}
