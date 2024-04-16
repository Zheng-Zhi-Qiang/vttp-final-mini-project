package backend.backend.repos;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import backend.backend.models.Listing;

@Repository
public class ListingRepo {
    
    @Autowired
    private MongoTemplate template;

    public String insertListing(Listing listing){
		Document listingDoc = listing.toDocument();
		Document doc = template.insert(listingDoc, "listings");
		return doc.getObjectId("_id").toHexString();
	}

    public Optional<Listing> getListingById(String listingId){
        ObjectId id = new ObjectId(listingId);
        Document doc = template.findById(id, Document.class, "listings");
        if (doc == null) {
            return Optional.empty();
        }
        return Optional.of(Listing.toListing(doc));
    }

    public boolean saveImgUrlToListing(String listingId, String imageUrl){
		ObjectId objId = new ObjectId(listingId);
		Criteria criteria = Criteria.where("_id").is(objId);
		Query query = Query.query(criteria);
		Update updateOps = new Update().push("images", imageUrl);
		UpdateResult result = template.updateMulti(query, updateOps, String.class, "listings");
		return result.getModifiedCount() == 1;
	}

    public List<Listing> getListingsByMapBoundary(Double neLat, Double swLat, Double neLng, Double swLng){
        Query query = new Query(Criteria.where("location.lat").gte(swLat).lte(neLat)
                                    .and("location.lng").gte(swLng).lte(neLng)
                                    .and("deleted").ne(true)
                                    .and("hidden").ne(true));
        return template.find(query, Document.class, "listings")
                        .stream()
                        .map(doc -> Listing.toListing(doc))
                        .toList();
    }

    public List<Listing> getListingsByUserId(String userId){
        Query query = new Query(Criteria.where("deleted").ne(true).and("lister_id").is(userId)); // not sure why regex doesn't work here
        return template.find(query, Document.class, "listings")
                        .stream()
                        .map(doc -> Listing.toListing(doc))
                        .toList();
    }

    public boolean deleteListing(String listingId){
		ObjectId objId = new ObjectId(listingId);
		Criteria criteria = Criteria.where("_id").is(objId);
		Query query = Query.query(criteria);
		Update updateOps = new Update().set("deleted", true);
		UpdateResult result = template.updateMulti(query, updateOps, Boolean.class, "listings");
		return result.getModifiedCount() == 1;
	}

    public boolean updateListingHiddenStatus(String listingId, Boolean status){
		ObjectId objId = new ObjectId(listingId);
		Criteria criteria = Criteria.where("_id").is(objId);
		Query query = Query.query(criteria);
		Update updateOps = new Update().set("hidden", status);
		UpdateResult result = template.updateMulti(query, updateOps, Boolean.class, "listings");
		return result.getModifiedCount() == 1;
	}

    public boolean fillListing(String listingId){
		ObjectId objId = new ObjectId(listingId);
		Criteria criteria = Criteria.where("_id").is(objId);
		Query query = Query.query(criteria);
		Update updateOps = new Update().set("filled", true).set("hidden", true);
		UpdateResult result = template.updateMulti(query, updateOps, Boolean.class, "listings");
		return result.getModifiedCount() == 1;
	}
}
