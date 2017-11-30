package ifood.score.jms;

import ifood.score.order.Order;
import ifood.score.service.RelevanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @Autowired
    private RelevanceService relevanceService;

    @JmsListener(destination = "${score.order.checkout.queue-name}", concurrency = "1-100")
    public void receiveCheckoutOrderMessage(Order order) {
        System.out.println("Receiving Checkout Order: " + order);

        relevanceService.processOrderCheckout(order);
    }

//    @JmsListener(destination = "${score.order.cancel.queue-name}")
    public void receiveCancelOrderMessage(String uuid) {
        System.out.println("Receiving Cancel Order: " + uuid);
    }
}
