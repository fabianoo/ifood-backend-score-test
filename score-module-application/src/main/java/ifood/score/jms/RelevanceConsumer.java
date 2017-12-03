package ifood.score.jms;

import com.google.gson.Gson;
import ifood.score.domain.entity.Relevance;
import ifood.score.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class RelevanceConsumer {

    @Autowired
    private ScoreService scoreService;

    @JmsListener(destination = "${score.relevance.queue-name}", concurrency = "1-100")
    public void receiveRelevance(String message) {
        Relevance relevance = new Gson().fromJson(message, Relevance.class);
        System.out.println("Receiving Category Relevance: " + relevance.getId());
        scoreService.computeRelevance(relevance);
    }
}
