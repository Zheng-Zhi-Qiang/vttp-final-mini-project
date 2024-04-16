package backend.backend.controllers;

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.backend.exceptions.BadRequestError;
import backend.backend.exceptions.NotAuthorisedError;
import backend.backend.models.Listing;
import backend.backend.services.ListingService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin
public class ListingRestController {

    @Autowired
    private ListingService listingSvc;

    @GetMapping(path = "/listing/{listingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getListing(@RequestParam String userId, @PathVariable String listingId){
        try {
            Optional<JsonObject> opt = listingSvc.getListingById(listingId, userId);
            if (opt.isEmpty()){
                String resp = Json.createObjectBuilder()
                                .add("error", "listing not found")
                                .build().toString();
                return ResponseEntity.status(404).body(resp);
            }
            else {
                return ResponseEntity.status(200).body(opt.get().toString());
            }
        } catch (Exception e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
                return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping(path = "/healthcheck")
    public ResponseEntity<String> healthCheck(){
        String resp = Json.createObjectBuilder()
                        .add("result", "healthy")
                        .build().toString();

        return ResponseEntity.status(200).body(resp);
    }

    @GetMapping(path = "/listings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getListingsByMapBoundaryAndPeriod(@RequestParam String neLat, @RequestParam String swLat,
                        @RequestParam String neLng, @RequestParam String swLng, @RequestParam Integer limit, @RequestParam Integer offset, @RequestParam String period){
        List<String> periodDates = Json.createReader(new StringReader(period)).readArray().stream().map(value -> value.toString().replace("\"", "")).toList();
        String resp = listingSvc.getListingsByMapBoundaryAndPeriod(Double.parseDouble(neLat), Double.parseDouble(swLat),
                                 Double.parseDouble(neLng), Double.parseDouble(swLng), periodDates, limit, offset).toString();
        return ResponseEntity.status(200).body(resp);
    }

    @PostMapping(path = "/listing", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody String body) {
        JsonObject data = Json.createReader(new StringReader(body)).readObject();
        Listing listing = Listing.toListing(data);
        try {
            String listingId = listingSvc.createListing(token.replaceFirst("Bearer ", ""), listing);
            String resp = Json.createObjectBuilder()
                                .add("listing_id", listingId)
                                .build().toString();
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping(path = "/listing/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadListingImages(@RequestPart MultipartFile[] files, @RequestPart String listingId){
        Optional<String> opt = this.listingSvc.uploadListingImages(listingId, files);
        if (opt.isEmpty()){
            String resp = Json.createObjectBuilder()
                                .add("error", "unable to upload images")
                                .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
        String resp = Json.createObjectBuilder()
                                .add("listing_id", opt.get())
                                .build().toString();
        return ResponseEntity.status(200).body(resp);
    }

    @GetMapping(path = "/user/listings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserListings(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String resp = listingSvc.getListingsByUserId(token).toString();
            return ResponseEntity.status(200).body(resp);
        }
        catch (ParseException e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping(path = "/listing/{listingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteListingByListingId(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String listingId){
        try {
            listingSvc.deleteListingById(listingId, token);
            return ResponseEntity.status(200).body(null);
        }
        catch (ParseException e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
        catch (BadRequestError e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(400).body(resp);
        }
        catch (NotAuthorisedError e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(403).body(resp);
        }
    }

    @PatchMapping(path = "/listing/{listingId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateListingHiddenStatusByListingId(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                    @PathVariable String listingId, @RequestBody String payload){
        try {
            Boolean status = Json.createReader(new StringReader(payload)).readObject().getBoolean("status");
            listingSvc.updateListingHiddenStatusByListingId(listingId, status, token);
            return ResponseEntity.status(200).body(null);
        }
        catch (ParseException e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(500).body(resp);
        }
        catch (BadRequestError e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(400).body(resp);
        }
        catch (NotAuthorisedError e) {
            String resp = Json.createObjectBuilder()
                                .add("error", e.getLocalizedMessage())
                                .build().toString();
            return ResponseEntity.status(403).body(resp);
        }
    }
}
