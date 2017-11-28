package ifood.score.domain.repository;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.entity.MenuScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryScoreRepository extends MongoRepository<CategoryScore, UUID> {

}
