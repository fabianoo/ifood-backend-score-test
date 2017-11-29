package ifood.score.service;

import ifood.score.domain.entity.Relevance;
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

public class RelevanceComputerTest {

    @Test
    public void testCalculateRelevance() {
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

        RelevanceComputer processor = new RelevanceComputer(order);
        List<Relevance> relevances = processor.relevances();

        Assert.assertEquals(1, relevances.size());
        Assert.assertEquals(BigDecimal.valueOf(100).setScale(1), relevances.get(0).getValue().setScale(1));
    }
}
