package backend.backend.services;

import com.stripe.Stripe;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import backend.backend.exceptions.BadRequestError;
import backend.backend.exceptions.DepositNotRequiredError;
import backend.backend.exceptions.EmailNotificationError;
import backend.backend.exceptions.ListingDoesNotExistError;
import backend.backend.exceptions.SQLInsertionError;
import backend.backend.models.Email;
import backend.backend.models.Listing;
import backend.backend.models.Transaction;
import backend.backend.models.User;
import backend.backend.repos.ListingRepo;
import backend.backend.repos.TransactionRepo;
import backend.backend.repos.UserRepo;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;


@Service
public class PaymentService {

    @Autowired
    private ListingRepo listingRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailSvc;

    @Autowired
    private Auth0UserService auth0Svc;

    @Value("${app.url}")
    private String url;

    @Value("${stripe.api.key}")
    private String stripeKey;
    
    public Session createPaymentSession(String listingId) throws ListingDoesNotExistError, DepositNotRequiredError, StripeException {
        // String listingId = data.getString("listing_id");
        Stripe.apiKey = stripeKey;
        Optional<Listing> listingOpt = listingRepo.getListingById(listingId);
        if (listingOpt.isEmpty()){
            throw new ListingDoesNotExistError("listing does not exist");
        }

        Listing listing = listingOpt.get();
        if (listing.getDeposit() == 0){
            throw new DepositNotRequiredError("deposit not required");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                                        .setMode(SessionCreateParams.Mode.PAYMENT)
                                        .setSuccessUrl("%s/payment/success".formatted(url))
                                        .setCancelUrl("%s/payment/cancelled".formatted(url))
                                        .addLineItem(
                                            SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            // Provide the exact Price ID (for example, pr_1234) of the product you want to sell
                                            .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmountDecimal(BigDecimal.valueOf(Double.parseDouble(listing.getDeposit().toString()) * 100))
                                                .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName("Deposit payment to %s for - %s %s, %s, %s %s".formatted(listing.getListerName(), listing.getAddress(), listing.getCountry(), listing.getState(), listing.getCity(), listing.getPostal()))
                                                    .putMetadata("Address", "test")
                                                    .build()
                                                )
                                                .build()
                                            )
                                            .build())
                                        .build();

        return Session.create(params);
    }

    public void createTransaction(Transaction transaction) throws SQLInsertionError, BadRequestError, EmailNotificationError {
        transactionRepo.insertTransaction(transaction);
        listingRepo.fillListing(transaction.getListingId());
        sendEmailNotificationToRecipient(transaction);
    }

    public JsonArray getUserTransactionsByUserId(String token) throws ParseException {
        String userId = auth0Svc.getUserIdFromJWT(token);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<Transaction> transactions = transactionRepo.getTransactionsByUserId(userId);
        transactions.stream().map(transaction -> transaction.toJson()).forEach(builder::add);
        return builder.build();
    }

    private void sendEmailNotificationToRecipient(Transaction transaction) throws BadRequestError, EmailNotificationError {
        Optional<User> opt = userRepo.getUserById(transaction.getPayer());
        if (opt.isEmpty()) {
            throw new BadRequestError("payer does not exists");
        }
        User payer = opt.get();
        opt = userRepo.getUserById(transaction.getPayee());
        if (opt.isEmpty()) {
            throw new BadRequestError("payee does not exists");
        }
        User payee = opt.get();
        int result = emailSvc.sendSimpleMail(
                Email.createPaymentNotificationEmail(payee.getEmail(), payer.getFirstName(), payee.getFirstName(), transaction.getListingId()));
        if (result == 1) {
            throw new EmailNotificationError("failed to send email");
        }
    }
}
