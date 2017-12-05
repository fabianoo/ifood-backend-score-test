package ifood.score.jms;

import com.google.gson.Gson;
import ifood.score.domain.entity.Relevance;
import ifood.score.domain.entity.RelevanceStatus;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.order.Order;
import ifood.score.service.RelevanceComputer;
import ifood.score.service.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RelevanceRepository relevanceRepository;

    @JmsListener(destination = "${score.order.checkout.queue-name}", concurrency = "1-100")
    @Transactional
    public void receiveCheckoutOrderMessage(String message) {
        logger.info("Receiving Checkout Order: " + message);
        Order order = new Gson().fromJson(message, Order.class);
        RelevanceComputer computer = new RelevanceComputer(order);
        Relevance relevance = computer.computeRelevance();

        relevance.getItems().forEach(r -> scoreService.computeRelevance(r));

        relevance.setStatus(RelevanceStatus.COMPUTED);
        relevanceRepository.save(relevance);
    }

    @JmsListener(destination = "${score.order.cancel.queue-name}", concurrency = "1-10")
    public void receiveCancelOrderMessage(String uuid) {
        logger.info("Receiving Cancel Order: " + uuid);
        scoreService.cancelRelevancesFromOrderId(uuid);
    }
}
