package ifood.score.domain.repository;

import ifood.score.domain.entity.MenuScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface MenuScoreRepository extends MongoRepository<MenuScore, UUID> {

    Page<MenuScore> findByMeanBetweenOrderByMeanAsc(BigDecimal min, BigDecimal max, Pageable pageable);
}
