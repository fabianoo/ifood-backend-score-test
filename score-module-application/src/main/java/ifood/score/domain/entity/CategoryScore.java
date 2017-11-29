package ifood.score.domain.entity;

import ifood.score.menu.Category;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryScore extends Score {
    @Id
    private Category category;
}
