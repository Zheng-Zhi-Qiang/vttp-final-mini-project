package backend.backend.repos;

import static backend.backend.repos.Queries.*;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import backend.backend.exceptions.SQLInsertionError;
import backend.backend.exceptions.SQLUpdateError;
import backend.backend.models.Conversation;
import backend.backend.models.Message;

@Repository
public class ConversationRepo {

    @Autowired
    private JdbcTemplate template;

    public void saveMessage(Message message) throws SQLInsertionError {
        int result = template.update(SQL_INSERT_MESSAGE, message.getConvoId(), message.getSender(),
                message.getReceiver(), message.getMessage(), new Timestamp(message.getDate().getTime()));
        if (result < 1) {
            throw new SQLInsertionError("Failed to create message");
        }
    }

    public Optional<Conversation> getConversationByConvoId(String id) {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_CONVO_BY_CONVO_ID, id);
        if (!rs.next()) {
            return Optional.empty();
        }
        return Optional.of(Conversation.toConversation(rs));
    }

    public void createConversation(Conversation convo) throws SQLInsertionError {
        int result = template.update(SQL_INSERT_CONVO, convo.getConvoId(), convo.getListingId(), convo.getUserId1(),
                convo.getUserId2());
        if (result < 1) {
            throw new SQLInsertionError("Failed to create convo");
        }
    }

    public List<Message> getMessagesByConvoId(String convoId) {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_MESSAGES_BY_CONVO_ID, convoId);
        List<Message> messages = new LinkedList<>();
        while (rs.next()) {
            messages.add(Message.toMessage(rs));
        }
        return messages;
    }

    public List<Conversation> getConversationsByUserId(String userId) {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_CONVOS_BY_USER_ID, userId, userId);
        List<Conversation> conversations = new LinkedList<>();
        while (rs.next()) {
            conversations.add(Conversation.toConversation(rs));
        }
        return conversations;
    }

    public Optional<Conversation> getConversationByUserIdsAndListingId(String userId1, String userId2,
            String listingId) {
        SqlRowSet rs = template.queryForRowSet(SQL_GET_CONVOS_BY_USER_IDS_AND_LISTING_ID, userId1, userId2, userId2,
                userId1, listingId);
        if (!rs.next()) {
            return Optional.empty();
        }
        return Optional.of(Conversation.toConversation(rs));
    }

    public void deleteConversationByConvoId(String convoId) throws SQLUpdateError {
        int rows = template.update(SQL_DELETE_CONVO_BY_CONVO_ID, convoId);
        if (rows < 1) {
            throw new SQLUpdateError("no rows updated");
        }
    }
}
