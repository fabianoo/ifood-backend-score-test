package ifood.score.domain.repository;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.menu.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryScoreRepository extends MongoRepository<CategoryScore, Category> {
}
