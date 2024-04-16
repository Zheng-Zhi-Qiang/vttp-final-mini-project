package backend.backend.services;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.backend.exceptions.BadRequestError;
import backend.backend.exceptions.NotAuthorisedError;
import backend.backend.models.Listing;
import backend.backend.repos.ListingRepo;
import backend.backend.repos.S3Repo;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ListingService {

    @Autowired
    private ListingRepo listingRepo;

    @Autowired
    private S3Repo s3Repo;

    @Autowired
    private Auth0UserService auth0Svc;

    public String createListing(String token, Listing listing) throws ParseException {
        String userId = auth0Svc.getUserIdFromJWT(token);
        listing.setListerId(userId);
        return listingRepo.insertListing(listing);
    }

    public Optional<JsonObject> getListingById(String listingId, String userId) {
        Optional<Listing> opt = listingRepo.getListingById(listingId);
        if (opt.isEmpty()){
            return Optional.empty();
        }
        else {
            Listing listing = opt.get();
            if (listing.getDeleted() || listing.getHidden()) {
                if (listing.getDeleted()) {
                    return Optional.empty();
                }
                else {
                    if (userId.equals("")) {
                        return Optional.empty();
                    }
                    else {
                        return listing.getListerId().equals(userId) ? Optional.of(listing.toJson()) : Optional.empty();
                    }
                }
            }
            return Optional.of(listing.toJson());
        }
    }

    public Optional<String> uploadListingImages(String listingId, MultipartFile[] imageFiles) {
        try {
            for (MultipartFile file: imageFiles){
                String imageId = UUID.randomUUID().toString().substring(0,8);
                String imageUrl = s3Repo.uploadListingImage(imageId, file);
                if (!listingRepo.saveImgUrlToListing(listingId, imageUrl)){
                    return Optional.empty();
                }
            }
            return Optional.of(listingId);
        }
        catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public JsonObject getListingsByMapBoundaryAndPeriod(Double neLat, Double swLat, Double neLng, Double swLng, List<String> period, Integer limit, Integer offset){
        List<Listing> listings = listingRepo.getListingsByMapBoundary(neLat, swLat, neLng, swLng);
        if (period.size() < 2){
            listings = listings.stream()
                                .filter(listing -> !(listing.getFromDate().before(Date.from(Instant.parse(period.get(0).toString())))))
                                .toList();
        }
        else {
            listings = listings.stream()
                                .filter(listing -> !(listing.getFromDate().before(Date.from(Instant.parse(period.get(0).toString()))) && listing.getToDate().after(Date.from(Instant.parse(period.get(1).toString())))))
                                .toList();
        }

        JsonArrayBuilder builder = Json.createArrayBuilder();
        limit = limit > listings.size() ? listings.size() : limit;
        listings.subList(offset, limit).stream().map(listing -> listing.toJson()).forEach(builder::add);
        return Json.createObjectBuilder()
                    .add("listings", builder.build())
                    .add("total_records", listings.size())
                    .build();
    }

    public JsonArray getListingsByUserId(String token) throws ParseException {
        String userId = auth0Svc.getUserIdFromJWT(token);
        List<Listing> listings = listingRepo.getListingsByUserId(userId);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        listings.stream().map(listing -> listing.toJson()).forEach(builder::add);
        return builder.build();
    }

    public void deleteListingById(String listingId, String token) throws ParseException, BadRequestError, NotAuthorisedError {
        Optional<Boolean> opt = validateListingOwner(listingId, token);
        if (opt.isEmpty()) {
            throw new BadRequestError("listing does not exist");
        }
        Boolean result = opt.get();
        if (!result) {
            throw new NotAuthorisedError("user not authorised to delete listing");
        }
        listingRepo.deleteListing(listingId);
    }

    public void updateListingHiddenStatusByListingId(String listingId, Boolean status, String token) throws ParseException, BadRequestError, NotAuthorisedError {
        Optional<Boolean> opt = validateListingOwner(listingId, token);
        if (opt.isEmpty()) {
            throw new BadRequestError("listing does not exist");
        }
        Boolean result = opt.get();
        if (!result) {
            throw new NotAuthorisedError("user not authorised to update listing");
        }
        listingRepo.updateListingHiddenStatus(listingId, status);
    }

    private Optional<Boolean> validateListingOwner(String listingId, String token) throws ParseException {
        String userId = auth0Svc.getUserIdFromJWT(token);
        Optional<Listing> opt = listingRepo.getListingById(listingId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Listing listing = opt.get();
        if (!listing.getListerId().equals(userId)) {
            return Optional.of(false);
        }

        return Optional.of(true);
    }
}
