package backend.backend.models;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class Listing {
    private String listerId;
    private String listingId;
    private Location location;
    private Date fromDate;
    private Date toDate;
    private String country;
    private String state;
    private String city;
    private String type;
    private String listerName;
    private String introduction;
    private String address;
    private String postal;
    private List<String> amenities;
    private Float deposit;
    private String apartmentDescription;
    private List<String> images = new LinkedList<>();
    private Boolean deleted;
    private Boolean hidden;
    private Boolean filled;

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public Date getFromDate() {
        return fromDate;
    }
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }
    public Date getToDate() {
        return toDate;
    }
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getListerName() {
        return listerName;
    }
    public void setListerName(String listerName) {
        this.listerName = listerName;
    }
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPostal() {
        return postal;
    }
    public void setPostal(String postal) {
        this.postal = postal;
    }
    public List<String> getAmenities() {
        return amenities;
    }
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
    public Float getDeposit() {
        return deposit;
    }
    public void setDeposit(Float deposit) {
        this.deposit = deposit;
    }
    public String getApartmentDescription() {
        return apartmentDescription;
    }
    public void setApartmentDescription(String apartmentDescription) {
        this.apartmentDescription = apartmentDescription;
    }
    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }
    public String getListingId() {
        return listingId;
    }
    public void setListingId(String listingId) {
        this.listingId = listingId;
    }
    public String getListerId() {
        return listerId;
    }
    public void setListerId(String listerId) {
        this.listerId = listerId;
    }
    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    public Boolean getHidden() {
        return hidden;
    }
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
    public Boolean getFilled() {
        return filled;
    }
    public void setFilled(Boolean filled) {
        this.filled = filled;
    }

    public static Listing toListing(Document doc){
        Listing listing = new Listing();
        listing.setListerId(doc.getString("lister_id"));
        listing.setListingId(doc.getObjectId("_id").toHexString());
        listing.setLocation(Location.toLocation(doc));
        listing.setFromDate(Date.from(Instant.parse(doc.getString("from"))));
        listing.setToDate(Date.from(Instant.parse(doc.getString("to"))));
        listing.setCountry(doc.getString("country"));
        listing.setCity(doc.getString("city"));
        listing.setState(doc.getString("state"));
        listing.setListerName(doc.getString("name"));
        listing.setIntroduction(doc.getString("introduction"));
        listing.setType(doc.getString("type"));
        listing.setAddress(doc.getString("address"));
        listing.setPostal(doc.getString("postal"));
        listing.setDeposit(Float.parseFloat(doc.get("deposit").toString()));
        listing.setAmenities(doc.getList("amenities", String.class));
        listing.setApartmentDescription(doc.getString("description"));
        listing.setImages(doc.getList("images", String.class));
        listing.setDeleted(doc.getBoolean("deleted"));
        listing.setHidden(doc.getBoolean("hidden"));
        listing.setFilled(doc.getBoolean("filled"));
        return listing;
    }

    public static Listing toListing(JsonObject data){
        Listing listing = new Listing();
        listing.setLocation(Location.toLocation(data.getJsonObject("location")));
        listing.setFromDate(Date.from(Instant.parse(data.getJsonObject("period").getString("from"))));
        listing.setToDate(Date.from(Instant.parse(data.getJsonObject("period").getString("to"))));
        listing.setCountry(data.getString("country"));
        listing.setCity(data.getString("city"));
        listing.setState(data.getString("state"));
        listing.setListerName(data.getString("name"));
        listing.setIntroduction(data.getString("introduction"));
        listing.setType(data.getString("type"));
        listing.setAddress(data.getString("address"));
        listing.setPostal(data.getString("postal"));
        listing.setDeposit(Float.parseFloat(data.get("deposit").toString()));
        List<String> amenities = new LinkedList<>();
        data.getJsonArray("amenities").forEach(value -> {
                amenities.add(value.toString().replaceAll("\"", ""));
            });
        listing.setAmenities(amenities);
        listing.setApartmentDescription(data.getString("description", ""));
        listing.setDeleted(data.getBoolean("deleted", false));
        listing.setHidden(data.getBoolean("hidden", false));
        listing.setFilled(data.getBoolean("filled", false));
        return listing;
    }

    public JsonObject toJson(){
        JsonArrayBuilder amenitiesArrayBuilder = Json.createArrayBuilder();
        amenities.stream().forEach(amenitiesArrayBuilder::add);
        JsonArrayBuilder imagesArrayBuilder = Json.createArrayBuilder();
        images.stream().forEach(imagesArrayBuilder::add);
        return Json.createObjectBuilder()
                    .add("lister_id", listerId)
                    .add("listing_id", listingId)
                    .add("location", location.toJson())
                    .add("period", Json.createObjectBuilder()
                        .add("from", fromDate.toInstant().toString())
                        .add("to", toDate.toInstant().toString()).build())
                    .add("country", country)
                    .add("state", state)
                    .add("city", city)
                    .add("name", listerName)
                    .add("introduction", introduction)
                    .add("type", type)
                    .add("address", address)
                    .add("postal", postal)
                    .add("deposit", deposit)
                    .add("amenities", amenitiesArrayBuilder.build())
                    .add("description", apartmentDescription)
                    .add("images", imagesArrayBuilder.build())
                    .add("hidden", hidden)
                    .add("deleted", deleted)
                    .add("filled", filled)
                    .build();
    }

    public Document toDocument(){
        JsonArrayBuilder amenitiesArrayBuilder = Json.createArrayBuilder();
        amenities.stream().forEach(amenitiesArrayBuilder::add);
        JsonArrayBuilder imagesArrayBuilder = Json.createArrayBuilder();
        images.stream().forEach(imagesArrayBuilder::add);
        String jsonString = Json.createObjectBuilder()
                                .add("lister_id", listerId)
                                .add("location", location.toJson())
                                .add("from", fromDate.toInstant().toString())
                                .add("to", toDate.toInstant().toString())
                                .add("country", country)
                                .add("state", state)
                                .add("city", city)
                                .add("name", listerName)
                                .add("introduction", introduction)
                                .add("type", type)
                                .add("address", address)
                                .add("postal", postal)
                                .add("deposit", deposit)
                                .add("amenities", amenitiesArrayBuilder.build())
                                .add("description", apartmentDescription)
                                .add("images", imagesArrayBuilder.build())
                                .add("hidden", hidden)
                                .add("deleted", deleted)
                                .add("filled", filled)
                                .build().toString();
        return Document.parse(jsonString);
    }
}
