package ifood.score.service;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.entity.MenuScore;
import ifood.score.menu.Category;
import ifood.score.order.Item;
import ifood.score.order.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderProcessor {

    private final Order order;
    private final Integer itemsTotalQty;
    private final BigDecimal totalPrice;
    private final Map<UUID, List<Item>> menuMap;
    private final Map<Category, List<Item>> categMap;

    OrderProcessor(Order order) {
        this.order = order;
        List<Item> allItems = order.getItems();
        this.itemsTotalQty = allItems.stream().mapToInt(Item::getQuantity).sum();
        this.totalPrice = allItems.stream()
                .map(i -> i.getMenuUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.menuMap = allItems.stream().collect(Collectors.groupingBy(Item::getMenuUuid));
        this.categMap = allItems.stream().collect(Collectors.groupingBy(Item::getMenuCategory));
    }

    List<MenuScore> menuScores() {
        return menuMap.keySet().stream().map(m -> {
            List<Item> items = menuMap.get(m);
            BigDecimal score = calculateScore(items);

            MenuScore menuScore = new MenuScore();
            menuScore.setScore(score);
            menuScore.setMenuId(m);
            menuScore.setConfirmedAt(order.getConfirmedAt());
            menuScore.setOrderId(order.getUuid());

            return menuScore;
        }).collect(Collectors.toList());
    }

    List<CategoryScore> categoryScores() {
        return categMap.keySet().stream().map(c -> {
            List<Item> items = categMap.get(c);
            BigDecimal score = calculateScore(items);

            CategoryScore categoryScore = new CategoryScore();
            categoryScore.setScore(score);
            categoryScore.setCategory(c);
            categoryScore.setConfirmedAt(order.getConfirmedAt());
            categoryScore.setOrderId(order.getUuid());

            return categoryScore;
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateScore(List<Item> items) {
        Integer menuQty = items.stream().mapToInt(Item::getQuantity).sum();
        BigDecimal menuPrice = items.stream()
                .map(i -> i.getMenuUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal qtyIndex = quantityIndex(itemsTotalQty, menuQty);
        BigDecimal priceIndex = priceIndex(totalPrice, menuPrice);
        return calculateRelevance(qtyIndex, priceIndex);
    }

    private BigDecimal calculateRelevance(BigDecimal qtyIndex, BigDecimal priceIndex) {
        BigDecimal total = qtyIndex.multiply(priceIndex).multiply(BigDecimal.valueOf(10000));
        double doubleValue = Math.sqrt(total.doubleValue());
        return BigDecimal.valueOf(doubleValue);
    }

    private BigDecimal quantityIndex(Integer allItemsQty, Integer itemsQty) {
        return BigDecimal.valueOf(itemsQty).divide(BigDecimal.valueOf(allItemsQty), BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal priceIndex(BigDecimal totalOrder, BigDecimal totalMenu)  {
        return totalMenu.divide(totalOrder, BigDecimal.ROUND_HALF_UP);
    }
}
