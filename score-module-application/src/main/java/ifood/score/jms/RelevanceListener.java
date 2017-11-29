package ifood.score.jms;

import ifood.score.domain.entity.Relevance;
import ifood.score.domain.exception.NotYetConsistentException;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RelevanceListener {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RelevanceRepository relevanceRepository;

    @JmsListener(destination = "${score.relevance.queue-name}", concurrency = "1-100")
    public void receiveRelevance(String id) throws NotYetConsistentException {
        System.out.println("Receiving Category Relevance: " + id);

        Optional<Relevance> relevance = relevanceRepository.findById(id);

        if(relevance.isPresent()) {
            scoreService.computeRelevance(relevance.get());
        } else {
            throw new NotYetConsistentException();
        }
    }
}
