package backend.backend.services;

import backend.backend.models.Listing;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import backend.backend.repos.FavouriteRepo;
import backend.backend.repos.ListingRepo;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

@Repository
public class FavouriteService {
    
    @Autowired
    private Auth0UserService auth0Svc;

    @Autowired
    private FavouriteRepo favRepo;

    @Autowired
    private ListingRepo listingRepo;

    public void saveFavourite(String listingId, String token) throws ParseException{
        String userId = auth0Svc.getUserIdFromJWT(token);
        favRepo.addFavourite(userId, listingId);
    }

    public void removeFavourite(String listingId, String token) throws ParseException{
        String userId = auth0Svc.getUserIdFromJWT(token);
        favRepo.removeFavourite(userId, listingId);
    }

    public JsonArray getFavouriteListings(String token) throws ParseException{
        String userId = auth0Svc.getUserIdFromJWT(token);
        List<String> listingIdList = favRepo.getFavourites(userId);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        listingIdList.stream()
                        .forEach(listingId -> {
                            Optional<Listing> opt = listingRepo.getListingById(listingId);
                            if (opt.isEmpty()){
                                return;
                            }
                            else {
                                Listing listing = opt.get();
                                if (listing.getDeleted() || listing.getHidden()) {
                                    return;
                                }
                                builder.add(listing.toJson());
                            }
                        });
        
        return builder.build();
    }

    public boolean checkIfFavourited(String listingId, String token) throws ParseException{
        String userId = auth0Svc.getUserIdFromJWT(token);
        List<String> listingIdList = favRepo.getFavourites(userId);
        return listingIdList.contains(listingId);
    }
}
