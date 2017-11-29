package ifood.score.domain.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public abstract class Score {

    private BigDecimal mean;

    private Integer weight;

    public void computeRelevance(OrderRelevance relevance) {
        BigDecimal amount = this.mean.multiply(BigDecimal.valueOf(weight));
        weight++;
        this.mean = amount.add(relevance.getValue()).divide(BigDecimal.valueOf(weight), BigDecimal.ROUND_HALF_UP);
    }
}
