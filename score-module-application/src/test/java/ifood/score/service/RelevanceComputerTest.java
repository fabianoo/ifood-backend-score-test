package ifood.score.service;

import ifood.score.domain.entity.Relevance;
import ifood.score.domain.utils.MathOperations;
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

        UUID menuUuid = UUID.randomUUID();
        Item item = new Item();
        item.setMenuUuid(menuUuid);
        item.setMenuCategory(Category.PIZZA);
        item.setMenuUnitPrice(MathOperations.asBigDecimal(50));
        item.setQuantity(2);

        order.setItems(Arrays.asList(item));

        RelevanceComputer processor = new RelevanceComputer(order);
        Relevance relevance = processor.computeRelevance();

        Assert.assertEquals(2, relevance.getItems().size());
        Assert.assertEquals(menuUuid, relevance.getItems().get(0).getMenuId());
        Assert.assertEquals(Category.PIZZA, relevance.getItems().get(1).getCategory());
        Assert.assertEquals(MathOperations.asBigDecimal(100), relevance.getItems().get(0).getValue());
        Assert.assertEquals(MathOperations.asBigDecimal(100), relevance.getItems().get(1).getValue());
    }
}
