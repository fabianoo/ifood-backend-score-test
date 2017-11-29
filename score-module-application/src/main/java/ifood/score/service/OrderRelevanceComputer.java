package ifood.score.service;

import ifood.score.domain.entity.OrderCategoryRelevance;
import ifood.score.domain.entity.OrderMenuRelevance;
import ifood.score.menu.Category;
import ifood.score.order.Item;
import ifood.score.order.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

class OrderRelevanceComputer {

    private final Order order;
    private final Integer itemsTotalQty;
    private final BigDecimal totalPrice;
    private final Map<UUID, List<Item>> menuMap;
    private final Map<Category, List<Item>> categMap;

    OrderRelevanceComputer(Order order) {
        this.order = order;
        List<Item> allItems = order.getItems();
        this.itemsTotalQty = allItems.stream().mapToInt(Item::getQuantity).sum();
        this.totalPrice = allItems.stream()
                .map(i -> i.getMenuUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.menuMap = allItems.stream().collect(Collectors.groupingBy(Item::getMenuUuid));
        this.categMap = allItems.stream().collect(Collectors.groupingBy(Item::getMenuCategory));
    }

    List<OrderMenuRelevance> menuRelevances() {
        return menuMap.keySet().stream().map(m -> {
            List<Item> items = menuMap.get(m);
            BigDecimal score = computeRelevance(items);

            OrderMenuRelevance orderMenuRelevance = new OrderMenuRelevance();
            orderMenuRelevance.setValue(score);
            orderMenuRelevance.setMenuId(m);
            orderMenuRelevance.setConfirmedAt(order.getConfirmedAt());
            orderMenuRelevance.setOrderId(order.getUuid());

            return orderMenuRelevance;
        }).collect(Collectors.toList());
    }

    List<OrderCategoryRelevance> categoryRelevances() {
        return categMap.keySet().stream().map(c -> {
            List<Item> items = categMap.get(c);
            BigDecimal score = computeRelevance(items);

            OrderCategoryRelevance orderCategoryRelevance = new OrderCategoryRelevance();
            orderCategoryRelevance.setValue(score);
            orderCategoryRelevance.setCategory(c);
            orderCategoryRelevance.setConfirmedAt(order.getConfirmedAt());
            orderCategoryRelevance.setOrderId(order.getUuid());

            return orderCategoryRelevance;
        }).collect(Collectors.toList());
    }

    private BigDecimal computeRelevance(List<Item> items) {
        Integer menuQty = items.stream().mapToInt(Item::getQuantity).sum();
        BigDecimal menuPrice = items.stream()
                .map(i -> i.getMenuUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal qtyIndex = quantityIndex(itemsTotalQty, menuQty);
        BigDecimal priceIndex = priceIndex(totalPrice, menuPrice);

        return relevance(qtyIndex, priceIndex);
    }

    private BigDecimal relevance(BigDecimal qtyIndex, BigDecimal priceIndex) {
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