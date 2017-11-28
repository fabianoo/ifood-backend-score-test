package ifood.score.domain.entity;

import ifood.score.menu.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Document
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryScore {

    @Indexed
    private UUID orderId;

    @Indexed
    private Category category;

    @Indexed
    private Date confirmedAt;

    private BigDecimal score;
}
