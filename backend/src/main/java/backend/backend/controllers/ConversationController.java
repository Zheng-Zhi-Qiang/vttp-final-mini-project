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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.backend.exceptions.BadRequestError;
import backend.backend.exceptions.EmailNotificationError;
import backend.backend.exceptions.NotAuthorisedError;
import backend.backend.exceptions.SQLInsertionError;
import backend.backend.exceptions.SQLUpdateError;
import backend.backend.models.Message;
import backend.backend.services.ConversationService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin
public class ConversationController {

    @Autowired
    private ConversationService convoSvc;

    @PostMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody String payload) {
        System.out.println(payload);
        JsonObject data = Json.createReader(new StringReader(payload)).readObject();
        Message message = Message.toMessage(data);
        try {
            String convoId = convoSvc.CreateMessage(token, message);
            String resp = Json.createObjectBuilder()
                    .add("convo_id", convoId)
                    .build().toString();
            return ResponseEntity.status(200).body(resp);
        } catch (NotAuthorisedError e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(403).body(resp);
        } catch (ParseException | SQLInsertionError | EmailNotificationError e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(500).body(resp);
        } catch (BadRequestError e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(400).body(resp);
        }
    }

    @GetMapping(path = "/conversation/{convoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getConversationById(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable String convoId) {
        try {
            Optional<JsonObject> opt = convoSvc.getConversationByConvoId(token, convoId);
            if (opt.isEmpty()) {
                String resp = Json.createObjectBuilder()
                        .add("status", 404)
                        .add("error", "conversation does not exist")
                        .build().toString();
                return ResponseEntity.status(404).body(resp);
            }
            return ResponseEntity.status(200).body(opt.get().toString());
        } catch (NotAuthorisedError e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("status", 403)
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(403).body(resp);
        } catch (ParseException e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("status", 500)
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping(path = "/conversations")
    public ResponseEntity<String> getConversationsByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String resp = convoSvc.getConversationsByUserId(token).toString();
            return ResponseEntity.status(200).body(resp);
        } catch (ParseException e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping(path = "/conversation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkConversationExists(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam("user_id_1") String userId1, @RequestParam("user_id_2") String userId2,
            @RequestParam("listing_id") String listingId) {

        try {
            Optional<JsonObject> opt = convoSvc.getConversationByUserIdsAndListingId(token, userId1, userId2,
                    listingId);
            if (opt.isEmpty()) {
                String resp = Json.createObjectBuilder()
                        .add("result", false)
                        .build().toString();
                return ResponseEntity.status(200).body(resp);
            }
            String resp = Json.createObjectBuilder()
                    .add("result", true)
                    .add("convo", opt.get())
                    .build().toString();
            return ResponseEntity.status(200).body(resp);
        } catch (NotAuthorisedError e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("status", 403)
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(403).body(resp);
        } catch (ParseException e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                    .add("status", 500)
                    .add("error", e.getLocalizedMessage())
                    .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping(path = "/conversation/{convoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteConvo(@PathVariable String convoId) {
        try {
            convoSvc.deleteConvoByConvoId(convoId);
            return ResponseEntity.status(200).body(null);
        }
        catch (SQLUpdateError e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(400).body(resp);
        }
        catch (Exception e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }
}
