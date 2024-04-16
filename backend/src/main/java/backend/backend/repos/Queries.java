package backend.backend.repos;

public class Queries {
        public static final String SQL_INSERT_USER = """
                        INSERT INTO user(user_id, email, first_name, last_name)
                        VALUE (?, ?, ?, ?);
                        """;

        public static final String SQL_GET_USER_BY_USER_ID = """
                        SELECT *
                        FROM user
                        WHERE user_id = ?;
                        """;

        public static final String SQL_INSERT_CONVO = """
                        INSERT INTO conversation(convo_id, listing_id, user_id_1, user_id_2)
                        VALUE (?, ?, ?, ?);
                        """;

        public static final String SQL_GET_CONVO_BY_CONVO_ID = """
                        SELECT *
                        FROM conversation
                        WHERE convo_id = ?
                        AND deleted = false;
                        """;

        public static final String SQL_INSERT_MESSAGE = """
                        INSERT INTO message(convo_id, sender, receiver, message, date)
                        VALUE (?, ?, ?, ?, ?);
                        """;

        public static final String SQL_INSERT_TRANSACTION = """
                        INSERT INTO transaction(listing_id, payee, payer, amount)
                        VALUE (?, ?, ?, ?);
                        """;

        public static final String SQL_GET_TRANSACTIONS_BY_USER_ID = """
                        SELECT * FROM transaction
                        WHERE payer = ?
                        OR payee = ?
                        ORDER BY date DESC;
                        """;

        public static final String SQL_GET_MESSAGES_BY_CONVO_ID = """
                        SELECT * FROM message
                        WHERE convo_id = ?
                        ORDER BY date;
                        """;

        public static final String SQL_GET_CONVOS_BY_USER_ID = """
                        SELECT * FROM conversation
                        WHERE (user_id_1 = ? OR user_id_2 = ?)
                        AND deleted = false;
                        """;

        public static final String SQL_GET_CONVOS_BY_USER_IDS_AND_LISTING_ID = """
                        SELECT * FROM conversation
                        WHERE ((user_id_1 = ? AND user_id_2 = ?)
                        OR (user_id_1 = ? AND user_id_2 = ?))
                        AND listing_id = ?
                        AND deleted = false;
                        """;

        public static final String SQL_DELETE_CONVO_BY_CONVO_ID = """
                        UPDATE conversation
                        SET deleted = true
                        WHERE convo_id = ?;
                        """;

        public static final String SQL_UPDATE_USER_PUSH_MESSAGING_TOKEN = """
                        UPDATE user
                        SET push_messaging_token = ?
                        WHERE user_id = ?;
                        """;
        public static final String SQL_GET_USER_PUSH_MESSAGING_TOKEN = """
                        SELECT push_messaging_token
                        FROM user
                        WHERE user_id = ?;
                        """;
}
