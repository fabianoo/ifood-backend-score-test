package ifood.score.domain.repository;

import ifood.score.domain.entity.FailedJmsMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FailedJmsMessageRepository extends MongoRepository<FailedJmsMessage, String> {

}
