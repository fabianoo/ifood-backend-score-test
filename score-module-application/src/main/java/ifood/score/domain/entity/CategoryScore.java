package ifood.score.domain.entity;

import ifood.score.menu.Category;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
@ToString
@NoArgsConstructor
public class CategoryScore extends Score {
    @Id
    private Category category;

    public CategoryScore(Category category) {
        super();
        this.category = category;
    }
}
