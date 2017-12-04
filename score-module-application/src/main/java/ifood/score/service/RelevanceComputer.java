package ifood.score.service;

import ifood.score.domain.entity.Relevance;
import ifood.score.domain.entity.RelevanceStatus;
import ifood.score.menu.Category;
import ifood.score.order.Item;
import ifood.score.order.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RelevanceComputer {

    private final Order order;
    private final Integer itemsTotalQty;
    private final BigDecimal totalPrice;
    private final Map<UUID, List<Item>> menuMap;
    private final Map<Category, List<Item>> categMap;
    private Relevance relevance = new Relevance();

    public RelevanceComputer(Order order) {
        this.order = order;
        List<Item> allItems = order.getItems();
        this.itemsTotalQty = allItems.stream().mapToInt(Item::getQuantity).sum();
        this.totalPrice = allItems.stream()
                .map(i -> i.getMenuUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.menuMap = allItems.stream().collect(Collectors.groupingBy(Item::getMenuUuid));
        this.categMap = allItems.stream().collect(Collectors.groupingBy(Item::getMenuCategory));

        this.relevance.setConfirmedAt(order.getConfirmedAt());
        this.relevance.setOrderId(order.getUuid());
        this.relevance.setStatus(RelevanceStatus.NOT_COMPUTED);
    }

    public Relevance computeRelevance() {
        computeMenuItems();
        computeCategoryItems();
        return relevance;
    }

    private void computeMenuItems() {
        menuMap.keySet().forEach(m -> {
            List<Item> items = menuMap.get(m);
            BigDecimal scoreItem = computeRelevanceValue(items);
            this.relevance.addItem(m, scoreItem);
        });
    }

    private void computeCategoryItems() {
        categMap.keySet().forEach(c -> {
            List<Item> items = categMap.get(c);
            BigDecimal scoreItem = computeRelevanceValue(items);
            this.relevance.addItem(c, scoreItem);
        });
    }

    private BigDecimal computeRelevanceValue(List<Item> items) {
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
