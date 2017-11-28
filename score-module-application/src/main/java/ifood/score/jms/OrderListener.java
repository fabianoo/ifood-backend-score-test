package ifood.score.jms;

import ifood.score.order.Order;
import ifood.score.service.OrderScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

    @Autowired
    private OrderScoreService orderScoreService;

    @JmsListener(destination = "checkout-order", concurrency = "1-100")
    public void receiveCheckoutOrderMessage(Order order) {
        System.out.println("Receiving Checkout Order: " + order);

        orderScoreService.processOrderCheckout(order);

    }

    @JmsListener(destination = "cancel-order")
    public void receiveCancelOrderMessage(String uuid) {
        System.out.println("Receiving Cancel Order: " + uuid);
    }
}
