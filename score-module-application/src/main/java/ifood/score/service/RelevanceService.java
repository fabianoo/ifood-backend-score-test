package ifood.score.service;

import ifood.score.domain.entity.Relevance;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.jms.JmsBridge;
import ifood.score.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelevanceService {

    @Autowired
    private RelevanceRepository relevanceRepository;

    @Autowired
    private JmsBridge jmsBridge;

    @Value("${score.relevance.queue-name}")
    private String relevanceQueue;

    public void processOrderCheckout(Order order) {
        RelevanceComputer computer = new RelevanceComputer(order);

        List<Relevance> relevances = computer.relevances();
        relevanceRepository.saveAll(relevances);

        relevances.forEach(r -> jmsBridge.sendMessage(relevanceQueue, r));
    }
}
