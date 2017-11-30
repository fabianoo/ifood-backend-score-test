package ifood.score.domain.repository;

import ifood.score.domain.entity.FailedJmsMessage;
import ifood.score.domain.entity.Relevance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedJmsMessageRepository extends MongoRepository<FailedJmsMessage, String> {

}
