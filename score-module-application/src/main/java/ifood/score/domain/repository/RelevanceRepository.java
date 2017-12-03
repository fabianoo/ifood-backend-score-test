package ifood.score.domain.repository;

import ifood.score.domain.entity.Relevance;
import ifood.score.domain.entity.RelevanceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelevanceRepository extends MongoRepository<Relevance, String> {

    List<Relevance> findByOrderIdAndStatus(String orderId, RelevanceStatus status);

    Integer countByOrderIdAndStatus(String orderId, RelevanceStatus status);

}
