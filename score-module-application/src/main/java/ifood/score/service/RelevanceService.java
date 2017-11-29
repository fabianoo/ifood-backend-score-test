package ifood.score.service;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import ifood.score.domain.entity.Relevance;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.menu.Category;
import ifood.score.order.Order;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RelevanceService {

    @Autowired
    private RelevanceRepository relevanceRepository;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${score.relevance.queue-name}")
    private String relevanceQueue;

    public void processOrderCheckout(Order order) {
        RelevanceComputer computer = new RelevanceComputer(order);

        List<Relevance> relevances = computer.relevances();
        relevanceRepository.saveAll(relevances);

        relevances.forEach(r -> jmsTemplate.convertAndSend(relevanceQueue, r.getId()));
    }

    @Deprecated
    public BigDecimal retrieveCategoryScoreMean(Category category) {
        List<BigDecimal> allScores = new ArrayList<>();
        Bson bson = Document.parse(String.format("{category: '%s'}", category.toString()));

        Block<BigDecimal> block = allScores::add;
        mongoClient.getDatabase("test").getCollection("categoryScore")
                .find(bson)
                .map(i -> new BigDecimal(i.get("score").toString())).forEach(block);
        Optional<BigDecimal> score = allScores.stream().reduce((s, accumulator) -> accumulator.add(s));
        return score.get().divide(BigDecimal.valueOf(allScores.size()), BigDecimal.ROUND_HALF_UP);
    }
}
