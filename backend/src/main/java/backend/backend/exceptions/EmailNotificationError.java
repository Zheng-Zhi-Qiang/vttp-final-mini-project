package backend.backend.exceptions;

public class EmailNotificationError extends Exception {
    public EmailNotificationError() {
        super();
    }

    public EmailNotificationError(String msg) {
        super(msg);
    }
}
