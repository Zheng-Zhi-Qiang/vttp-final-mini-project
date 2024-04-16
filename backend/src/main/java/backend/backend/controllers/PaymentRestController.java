package backend.backend.controllers;

import java.io.StringReader;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.checkout.Session;

import backend.backend.models.Transaction;
import backend.backend.services.PaymentService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin
public class PaymentRestController {

    @Autowired
    private PaymentService pmtSvc;

    @GetMapping(path = "/payment/{listingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createPaymentSession(@PathVariable String listingId) {
        System.out.println("reached resource");
        try {
            Session session = pmtSvc.createPaymentSession(listingId);
            String resp = Json.createObjectBuilder()
                                .add("location", session.getUrl())
                                .build().toString();
            return ResponseEntity.status(200).body(resp);
        }
        catch (Exception e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(400).body(resp);
        }
    }
    
    @PostMapping(path = "/payment/success", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> recordTransaction(@RequestBody String payload) {
        JsonObject data = Json.createReader(new StringReader(payload)).readObject();
        Transaction transaction = Transaction.toTransaction(data);
        try {
            pmtSvc.createTransaction(transaction);
            return ResponseEntity.status(201).body(null);
        }
        catch (Exception e){
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping(path = "/user/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserTransactions(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        try {
            String resp = pmtSvc.getUserTransactionsByUserId(token).toString();
            return ResponseEntity.status(200).body(resp);
        }
        catch (ParseException e){
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }
}
