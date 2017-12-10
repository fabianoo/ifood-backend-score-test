package ifood.score.test.integration.setup;

import ifood.score.menu.Category;
import ifood.score.order.Item;
import ifood.score.order.Order;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static ifood.score.domain.utils.MathOperations.*;

@Repository
public class OrderRepository {


    public Order onePizzaOrder() {
        Order order = new Order();
        order.setRestaurantUuid(UUID.randomUUID());
        order.setUuid(UUID.randomUUID());
        order.setCustomerUuid(UUID.randomUUID());
        order.setAddressUuid(UUID.randomUUID());
        order.setConfirmedAt(new Date());
        order.setItems(Collections.singletonList(pizza()));
        return order;
    }

    private Item pizza() {
        Item pizza = new Item();
        pizza.setMenuUuid(UUID.randomUUID());
        pizza.setQuantity(1);
        pizza.setMenuUnitPrice(asBigDecimal(70));
        pizza.setMenuCategory(Category.PIZZA);
        return pizza;
    }

}
