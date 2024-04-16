package backend.backend.services;

import java.text.ParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.backend.exceptions.SQLInsertionError;
import backend.backend.exceptions.SQLUpdateError;
import backend.backend.models.User;
import backend.backend.repos.UserRepo;
import jakarta.json.JsonObject;

@Service
public class UserService {

    @Autowired
    private Auth0UserService auth0UserSvc;

    @Autowired
    private UserRepo userRepo;

    public String createUser(JsonObject data, String token) throws ParseException, SQLInsertionError {
        String user_id = auth0UserSvc.getUserIdFromJWT(token);
        User user = User.toUser(data);
        String email = auth0UserSvc.getUserEmail(user_id);
        user.setEmail(email);
        user.setId(user_id);
        userRepo.insertUser(user);
        System.out.printf(">>> user_id: %s\n", user_id);
        return user_id;
    }

    public Optional<User> userExists(String token) throws ParseException {
        String user_id = auth0UserSvc.getUserIdFromJWT(token);
        return userRepo.getUserById(user_id);
    }

    public Optional<JsonObject> getUserById(String userId) {
        Optional<User> opt = userRepo.getUserById(userId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(opt.get().toJson());
    }

    public void insertUserMessagePushToken(String jwtToken, String token) throws ParseException, SQLUpdateError {
        String userId = auth0UserSvc.getUserIdFromJWT(jwtToken);
        userRepo.updateUserPushMessagingToken(userId, token);
    }

    public Optional<String> getUserMessagePushToken(String userId) {
        return userRepo.getUserPushMessagingToken(userId);
    }
}
