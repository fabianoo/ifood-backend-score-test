package ifood.score.domain.repository;

import ifood.score.domain.entity.Relevance;
import ifood.score.domain.entity.RelevanceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface RelevanceRepository extends MongoRepository<Relevance, UUID> {

    List<Relevance> findByConfirmedAtBeforeAndStatus(Date confirmedUntil, RelevanceStatus status);

    List<Relevance> findByStatus(RelevanceStatus status);
}
