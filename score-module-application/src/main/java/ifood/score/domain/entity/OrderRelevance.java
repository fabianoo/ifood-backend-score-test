package ifood.score.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
public abstract class OrderRelevance {

    @Id
    private UUID id;

    @Indexed
    private UUID orderId;

    @Indexed
    private Date confirmedAt;

    private BigDecimal value;

    private RelevanceStatus status;
}
