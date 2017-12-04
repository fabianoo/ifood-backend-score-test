package ifood.score.domain.repository;

import ifood.score.domain.entity.Relevance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RelevanceRepository extends MongoRepository<Relevance, UUID> {
//    Optional<Relevance> findByOrderId(String orderId, RelevanceStatus status);

}
