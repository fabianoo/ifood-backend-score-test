package ifood.score.domain.repository;

import ifood.score.domain.entity.OrderCategoryRelevance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryScoreRepository extends MongoRepository<OrderCategoryRelevance, UUID> {

}
