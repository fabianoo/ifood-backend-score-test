package ifood.score.domain.entity;

import ifood.score.menu.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Relevance {

    @Id
    private UUID orderId;

    private RelevanceStatus status;

    private Date confirmedAt;

    private List<Item> items = new ArrayList<>();

    public void addItem(Category category, BigDecimal value) {
        items.add(new Item(category, value));
    }

    public void addItem(UUID menu, BigDecimal value) {
        items.add(new Item(menu, value));
    }

    @Data
    @NoArgsConstructor
    public static class Item {
        @Indexed
        private Category category;

        @Indexed
        private UUID menuId;

        private BigDecimal value;

        public Item(Category category, BigDecimal value) {
            this.category = category;
            this.value = value;
        }

        public Item(UUID menuId, BigDecimal value) {
            this.menuId = menuId;
            this.value = value;
        }
    }
}
