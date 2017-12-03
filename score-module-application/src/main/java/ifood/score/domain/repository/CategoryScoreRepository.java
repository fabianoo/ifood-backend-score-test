package ifood.score.domain.repository;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.menu.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CategoryScoreRepository extends MongoRepository<CategoryScore, Category> {

    List<CategoryScore> findByMeanBetweenOrderByMeanAsc(BigDecimal min, BigDecimal max);

    @Override
    List<CategoryScore> findAllById(Iterable<Category> categories);
}
