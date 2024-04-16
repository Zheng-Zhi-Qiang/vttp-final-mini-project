package backend.backend.repos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FavouriteRepo {
    
    @Autowired @Qualifier("redis")
    private RedisTemplate<String, String> template;

    public void addFavourite(String userId, String listingId) {
        ListOperations<String, String> listOps = template.opsForList(); // cannot instantiate it outside of functions since template will not be instatiated yet
        listOps.rightPush(userId, listingId);
    }

    public List<String> getFavourites(String userId) {
        ListOperations<String, String> listOps = template.opsForList();
        return listOps.range(userId, 0, -1);
    }

    public void removeFavourite(String userId, String listingId) {
        ListOperations<String, String> listOps = template.opsForList();
        listOps.remove(userId, 1, listingId);
    }
    
}
