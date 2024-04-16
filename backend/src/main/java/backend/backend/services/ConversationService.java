package backend.backend.services;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import backend.backend.exceptions.BadRequestError;
import backend.backend.exceptions.EmailNotificationError;
import backend.backend.exceptions.NotAuthorisedError;
import backend.backend.exceptions.SQLInsertionError;
import backend.backend.exceptions.SQLUpdateError;
import backend.backend.models.Conversation;
import backend.backend.models.Email;
import backend.backend.models.Message;
import backend.backend.models.User;
import backend.backend.repos.ConversationRepo;
import backend.backend.repos.UserRepo;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Service
public class ConversationService {

    @Autowired
    private Auth0UserService auth0UserSvc;

    @Autowired
    private ConversationRepo convoRepo;

    @Autowired
    private MessageSender msgSender;

    @Autowired
    private UserService userSvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailSvc;

    @Value("${fcm.server.key}")
    private String fcmServerKey;

    @Value("${app.url}")
    private String url;

    private RestTemplate template = new RestTemplate();

    private String fcmUrl = "https://fcm.googleapis.com/fcm/send";

    @Transactional(rollbackFor = SQLInsertionError.class)
    public String CreateMessage(String token, Message message)
            throws SQLInsertionError, NotAuthorisedError, ParseException, BadRequestError, EmailNotificationError {
        String userId = auth0UserSvc.getUserIdFromJWT(token);
        if (!userId.equals(message.getSender()) && !userId.equals(message.getReceiver())) {
            throw new NotAuthorisedError("user not authorised to send message");
        }
        if (userId.equals(message.getReceiver())) {
            throw new BadRequestError("user cannot send a message to himself");
        }

        Optional<Conversation> opt = convoRepo.getConversationByConvoId(message.getConvoId());
        Conversation convo;
        if (opt.isEmpty()) {
            convo = new Conversation();
            convo.setConvoId(message.getConvoId());
            convo.setListingId(message.getListingId());
            convo.setUserId1(message.getSender());
            convo.setUserId2(message.getReceiver());
            convoRepo.createConversation(convo);
        } else {
            convo = opt.get();
        }

        if (!convo.getUserId1().equals(userId) && !convo.getUserId2().equals(userId)) {
            throw new NotAuthorisedError("user not authorised to join conversation");
        }

        convoRepo.saveMessage(message);
        pushMessageToClient(message);
        sendEmailNotificationToRecipient(message);
        msgSender.sendMessage(message);
        return message.getConvoId();
    }

    public JsonArray getConversationsByUserId(String token) throws ParseException {
        String userId = auth0UserSvc.getUserIdFromJWT(token);
        List<Conversation> conversations = convoRepo.getConversationsByUserId(userId);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        conversations.stream().forEach(convo -> {
            String otherUserId = convo.getUserId1().equals(userId) ? convo.getUserId2() : convo.getUserId1();
            Optional<JsonObject> opt = userSvc.getUserById(otherUserId);
            if (opt.isEmpty()) {
                return;
            }
            builder.add(Json.createObjectBuilder()
                    .add("convo", convo.toShortJson())
                    .add("other_user", opt.get())
                    .build());
        });
        return builder.build();
    }

    public Optional<JsonObject> getConversationByConvoId(String token, String convoId)
            throws ParseException, NotAuthorisedError {
        String userId = auth0UserSvc.getUserIdFromJWT(token);
        Optional<Conversation> opt = convoRepo.getConversationByConvoId(convoId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        Conversation convo = opt.get();
        if (!convo.getUserId1().equals(userId) && !convo.getUserId2().equals(userId)) {
            throw new NotAuthorisedError("user not authorised to retrieve conversation");
        }

        List<Message> messages = convoRepo.getMessagesByConvoId(convoId);
        convo.setMessages(messages);
        return Optional.of(convo.toJson());
    }

    public Optional<JsonObject> getConversationByUserIdsAndListingId(String token, String user1, String user2,
            String listingId) throws ParseException, NotAuthorisedError {
        String userId = auth0UserSvc.getUserIdFromJWT(token);
        Optional<Conversation> opt = convoRepo.getConversationByUserIdsAndListingId(user1, user2, listingId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        Conversation convo = opt.get();
        if (!convo.getUserId1().equals(userId) && !convo.getUserId2().equals(userId)) {
            throw new NotAuthorisedError("user not authorised to retrieve conversation");
        }

        List<Message> messages = convoRepo.getMessagesByConvoId(convo.getConvoId());
        convo.setMessages(messages);
        return Optional.of(convo.toJson());
    }

    public void deleteConvoByConvoId(String convoId) throws SQLUpdateError {
        convoRepo.deleteConversationByConvoId(convoId);
    }

    private void pushMessageToClient(Message message) {
        Optional<String> opt = userSvc.getUserMessagePushToken(message.getReceiver());
        if (opt.isPresent()) {
            String token = opt.get();
            System.out.printf("token, %s\n", token);
            JsonObject payload = Json.createObjectBuilder()
                    .add("notification",
                            Json.createObjectBuilder()
                                    .add("title", "Notification from %s".formatted(message.getSender()))
                                    .add("body", message.toJson())
                                    .build())
                    .add("to", token)
                    .build();
            String url = UriComponentsBuilder.fromUriString(fcmUrl)
                    .build().toUriString();
            RequestEntity<String> req = RequestEntity
                    .post(url)
                    .header("Authorization", "key=%s".formatted(fcmServerKey))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload.toString(), String.class);
            template.exchange(req, String.class);
        }
    }

    private void sendEmailNotificationToRecipient(Message message) throws BadRequestError, EmailNotificationError {
        Optional<User> opt = userRepo.getUserById(message.getSender());
        if (opt.isEmpty()) {
            throw new BadRequestError("sender does not exists");
        }
        User sender = opt.get();
        opt = userRepo.getUserById(message.getReceiver());
        if (opt.isEmpty()) {
            throw new BadRequestError("receiver does not exists");
        }
        User receiver = opt.get();
        String senderName = sender.getFirstName();
        int result = emailSvc.sendSimpleMail(
                Email.createMessageNotificationEmail(receiver.getEmail(), senderName, receiver.getFirstName(), url));
        if (result == 1) {
            throw new EmailNotificationError("failed to send email");
        }
    }

}
