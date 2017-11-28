package ifood.score.service;

import ifood.score.domain.entity.CategoryScore;
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

public class OrderProcessorTest {

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

        OrderProcessor processor = new OrderProcessor(order);
        List<CategoryScore> categoryScores = processor.categoryScores();

        Assert.assertEquals(1, categoryScores.size());
        Assert.assertEquals(BigDecimal.valueOf(100).setScale(1), categoryScores.get(0).getScore().setScale(1));
    }
}
