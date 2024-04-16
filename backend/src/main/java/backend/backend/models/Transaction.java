package backend.backend.models;

import java.util.Date;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Transaction {
    private Long id;
    private String payer;
    private String payee;
    private String listingId;
    private Float amount;
    private Date date;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPayer() {
        return payer;
    }
    public void setPayer(String payer) {
        this.payer = payer;
    }
    public String getPayee() {
        return payee;
    }
    public void setPayee(String payee) {
        this.payee = payee;
    }
    public Float getAmount() {
        return amount;
    }
    public void setAmount(Float amount) {
        this.amount = amount;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getListingId() {
        return listingId;
    }
    public void setListingId(String listingId) {
        this.listingId = listingId;
    }
    

    public static Transaction toTransaction(JsonObject data){
        Transaction transaction = new Transaction();
        transaction.setAmount(Float.parseFloat(data.get("amount").toString()));
        transaction.setListingId(data.getString("listing_id"));
        transaction.setPayee(data.getString("payee"));
        transaction.setPayer(data.getString("payer"));
        return transaction;
    }

    public static Transaction toTransaction(SqlRowSet rs){
        Transaction transaction = new Transaction();
        transaction.setAmount(rs.getFloat("amount"));
        transaction.setPayee(rs.getString("payee"));
        transaction.setPayer(rs.getString("payer"));
        transaction.setId(rs.getLong("transaction_id"));
        transaction.setDate(new Date(rs.getDate("date").getTime()));
        transaction.setListingId(rs.getString("listing_id"));
        return transaction;
    }

    public JsonObject toJson(){
        return Json.createObjectBuilder()
                    .add("transaction_id", id)
                    .add("listing_id", listingId)
                    .add("payee", payee)
                    .add("payer", payer)
                    .add("amount", amount)
                    .add("date", date.toInstant().toString())
                    .build();
    }
}
