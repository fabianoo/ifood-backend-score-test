package ifood.score.jms;

import ifood.score.order.Order;
import ifood.score.service.OrderCheckoutService;
import ifood.score.service.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private OrderCheckoutService orderCheckoutService;

    @Autowired
    private ScoreService scoreService;

    @JmsListener(destination = "${score.order.checkout.queue-name}", concurrency = "1-100")
    public void receiveCheckoutOrderMessage(Order order) {
        logger.info("Receiving Checkout Order: " + order);
        orderCheckoutService.processOrderCheckout(order);
    }

    @JmsListener(destination = "${score.order.cancel.queue-name}", concurrency = "1-10")
    public void receiveCancelOrderMessage(String uuid) {
        logger.info("Receiving Cancel Order: " + uuid);
        scoreService.cancelRelevancesFromOrderId(uuid);
    }
}
