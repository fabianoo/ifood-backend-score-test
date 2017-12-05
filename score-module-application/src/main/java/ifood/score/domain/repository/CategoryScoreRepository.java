package ifood.score.domain.repository;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.menu.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryScoreRepository extends MongoRepository<CategoryScore, Category> {
}
