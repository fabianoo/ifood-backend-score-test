package ifood.score.domain.repository;

import ifood.score.domain.entity.OrderMenuRelevance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MenuScoreRepository extends MongoRepository<OrderMenuRelevance, UUID> {

}
