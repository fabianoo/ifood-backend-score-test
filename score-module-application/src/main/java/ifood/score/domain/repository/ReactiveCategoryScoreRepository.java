package ifood.score.domain.repository;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.menu.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReactiveCategoryScoreRepository extends ReactiveMongoRepository<CategoryScore, Category> {

    Flux<CategoryScore> findByMeanBetweenOrderByMeanAsc(BigDecimal min, BigDecimal max);
}
