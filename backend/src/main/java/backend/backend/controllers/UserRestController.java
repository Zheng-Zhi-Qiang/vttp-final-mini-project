package backend.backend.controllers;

import java.io.StringReader;
import java.text.ParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.backend.exceptions.SQLUpdateError;
import backend.backend.models.User;
import backend.backend.services.FavouriteService;
import backend.backend.services.UserService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin
public class UserRestController {
    @Autowired
    private UserService userSvc;

    @Autowired
    private FavouriteService favSvc;

    @PostMapping(path = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody String payload) {
        JsonObject data = Json.createReader(new StringReader(payload)).readObject();
        try {
            String userId = userSvc.createUser(data, token);
            String resp = Json.createObjectBuilder()
                    .add("user_id", userId)
                    .build().toString();
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping(path = "/user_check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> userExists(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            Optional<User> opt = userSvc.userExists(token);
            if (opt.isEmpty()) {
                String resp = Json.createObjectBuilder()
                        .add("result", false)
                        .build().toString();
                return ResponseEntity.status(200).body(resp);
            } else {
                String resp = Json.createObjectBuilder()
                        .add("result", true)
                        .add("user_id", opt.get().getId())
                        .build().toString();
                return ResponseEntity.status(200).body(resp);
            }
        } catch (Exception e) {
            String resp = Json.createObjectBuilder()
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping(path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserById(@PathVariable String userId) {
        Optional<JsonObject> opt = userSvc.getUserById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.status(200).body(opt.get().toString());
    }

    @PatchMapping(path = "/user/messaging", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUserPushMessagingToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken,
        @RequestBody String payload){
        String token = Json.createReader(new StringReader(payload)).readObject().getString("token");
        try {
            userSvc.insertUserMessagePushToken(jwtToken, token);
            return ResponseEntity.status(200).body(null);
        }
        catch (ParseException e){
            String resp = Json.createObjectBuilder()
                            .add("error", e.getLocalizedMessage())
                            .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
        catch (SQLUpdateError e){
            String resp = Json.createObjectBuilder()
                            .add("error", e.getLocalizedMessage())
                            .build().toString();
            return ResponseEntity.status(400).body(resp);
        }
    }

    @GetMapping(path = "/user/favourites", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserFavourites(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String resp = favSvc.getFavouriteListings(token).toString();
            return ResponseEntity.status(200).body(resp);
        }
        catch (ParseException e) {
            String resp = Json.createObjectBuilder()
                            .add("error", e.getLocalizedMessage())
                            .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PatchMapping(path = "/user/favourite/{listingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addUserFavourite(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String listingId) {
        try {
            favSvc.saveFavourite(listingId, token);
            return ResponseEntity.status(200).body(null);
        }
        catch (ParseException e) {
            String resp = Json.createObjectBuilder()
                            .add("error", e.getLocalizedMessage())
                            .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }


    @DeleteMapping(path = "/user/favourite/{listingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteUserFavourite(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String listingId) {
        try {
            favSvc.removeFavourite(listingId, token);
            return ResponseEntity.status(200).body(null);
        }
        catch (ParseException e) {
            String resp = Json.createObjectBuilder()
                            .add("error", e.getLocalizedMessage())
                            .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping(path = "/user/favourite/{listingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listingFavourited(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String listingId){
        try {
            boolean result = favSvc.checkIfFavourited(listingId, token);
            String resp = Json.createObjectBuilder()
                                .add("result", result)
                                .build().toString();
            return ResponseEntity.status(200).body(resp);
        }
        catch (ParseException e) {
            String resp = Json.createObjectBuilder()
                            .add("error", e.getLocalizedMessage())
                            .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }
}
