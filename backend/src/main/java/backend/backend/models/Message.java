package backend.backend.models;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Message {
    private String convoId;
    private String listingId;
    private String sender;
    private String receiver;
    private String message;
    private Date date;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getConvoId() {
        return convoId;
    }

    public void setConvoId(String convoId) {
        this.convoId = convoId;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public static Message toMessage(JsonObject data) {
        Message message = new Message();
        message.setDate(Date.from(Instant.parse(data.getString("date"))));
        message.setConvoId(data.getString("convo_id", UUID.randomUUID().toString().substring(0, 8)));
        message.setListingId(data.getString("listing_id", null));
        message.setSender(data.getString("sender"));
        message.setReceiver(data.getString("receiver"));
        message.setMessage(data.getString("message"));
        return message;
    }

    public static Message toMessage(SqlRowSet rs) {
        Message message = new Message();
        message.setDate(new Date(rs.getDate("date").getTime()));
        message.setConvoId(rs.getString("convo_id"));
        message.setSender(rs.getString("sender"));
        message.setReceiver(rs.getString("receiver"));
        message.setMessage(rs.getString("message"));
        return message;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("date", date.toInstant().toString())
                .add("convo_id", convoId)
                .add("sender", sender)
                .add("receiver", receiver)
                .add("message", message)
                .build();
    }
}
