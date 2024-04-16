package backend.backend.models;

import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class Conversation {
    private String convoId;
    private List<Message> messages;
    private String userId1;
    private String userId2;
    private String listingId;
    private Boolean deleted;

    public String getConvoId() {
        return convoId;
    }

    public void setConvoId(String id) {
        this.convoId = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public JsonObject toJson() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        messages.stream().map(msg -> msg.toJson()).forEach(builder::add);
        return Json.createObjectBuilder()
                .add("convo_id", convoId)
                .add("user_id_1", userId1)
                .add("user_id_2", userId2)
                .add("listing_id", listingId)
                .add("messages", builder.build())
                .add("deleted", deleted)
                .build();
    }

    public static Conversation toConversation(JsonObject data) {
        Conversation convo = new Conversation();
        convo.setUserId1(data.getString("user_id_1"));
        convo.setUserId2(data.getString("user_id_2"));
        convo.setListingId(data.getString("listing_id"));
        return convo;
    }

    public static Conversation toConversation(SqlRowSet rs) {
        Conversation convo = new Conversation();
        convo.setConvoId(rs.getString("convo_id"));
        convo.setUserId1(rs.getString("user_id_1"));
        convo.setUserId2(rs.getString("user_id_2"));
        convo.setListingId(rs.getString("listing_id"));
        convo.setDeleted(rs.getBoolean("deleted"));
        return convo;
    }

    public JsonObject toShortJson() {
        return Json.createObjectBuilder()
                .add("convo_id", convoId)
                .add("user_id_1", userId1)
                .add("user_id_2", userId2)
                .add("deleted", deleted)
                .add("listing_id", listingId)
                .build();
    }
}
