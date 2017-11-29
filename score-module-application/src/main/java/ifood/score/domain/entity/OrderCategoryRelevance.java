package ifood.score.domain.entity;

import ifood.score.menu.Category;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderCategoryRelevance extends OrderRelevance {

    @Indexed
    private Category category;
}
