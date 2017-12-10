package ifood.score.domain.repository;

import ifood.score.domain.entity.MenuScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ReactiveMenuScoreRepository extends ReactiveMongoRepository<MenuScore, UUID> {

    Flux<MenuScore> findByMeanBetweenOrderByMeanAsc(BigDecimal min, BigDecimal max, Pageable pageable);
}
