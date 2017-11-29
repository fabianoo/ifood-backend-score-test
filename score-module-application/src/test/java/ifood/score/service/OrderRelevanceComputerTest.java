package ifood.score.service;

import ifood.score.domain.entity.OrderCategoryRelevance;
import ifood.score.menu.Category;
import ifood.score.order.Item;
import ifood.score.order.Order;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderRelevanceComputerTest {

    @Test
    public void testCalculateScore() {
        Order order = new Order();
        order.setUuid(UUID.randomUUID());
        order.setAddressUuid(UUID.randomUUID());
        order.setCustomerUuid(UUID.randomUUID());
        order.setRestaurantUuid(UUID.randomUUID());
        order.setConfirmedAt(new Date());

        Item item = new Item();
        item.setMenuUuid(UUID.randomUUID());
        item.setMenuCategory(Category.PIZZA);
        item.setMenuUnitPrice(new BigDecimal(50));
        item.setQuantity(2);

        order.setItems(Arrays.asList(item));

        OrderRelevanceComputer processor = new OrderRelevanceComputer(order);
        List<OrderCategoryRelevance> orderCategoryRelevances = processor.categoryRelevances();

        Assert.assertEquals(1, orderCategoryRelevances.size());
        Assert.assertEquals(BigDecimal.valueOf(100).setScale(1), orderCategoryRelevances.get(0).getValue().setScale(1));
    }
}
