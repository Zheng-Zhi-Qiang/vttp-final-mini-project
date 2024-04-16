package backend.backend.models;

public class Email {
    private String subject;
    private String recipient;
    private String message;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Email createMessageNotificationEmail(String recipient, String senderName, String receiverName, String url) {
        Email email = new Email();
        email.setSubject("New Message Received");
        email.setRecipient(recipient);
        email.setMessage("Hello %s!\n\nYou have a new message from %s!\nYou can view your messages by clicking on the following link or by logging in to your account!\n%s\n\nBest Regards,\nBuddyFinder Team".formatted(receiverName, senderName, "%s/conversations".formatted(url)));
        return email;
    }

    public static Email createPaymentNotificationEmail(String recipient, String payerName, String payeeName, String listingId) {
        Email email = new Email();
        email.setSubject("Payment Made For Listing %s".formatted(listingId));
        email.setRecipient(recipient);
        email.setMessage("Hello %s!\n\nPayment has been made by %s for listing %s!\nThe listing has been closed.\n\nBest Regards,\nBuddyFinder Team".formatted(payeeName, payerName, listingId));
        return email;
    }
}