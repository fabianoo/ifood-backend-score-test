package ifood.score.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public abstract class Score {

    private BigDecimal mean;

    private Integer weight;

    public void computeRelevance(Relevance relevance) {
        BigDecimal amount = this.mean.multiply(BigDecimal.valueOf(weight));
        weight++;
        this.mean = amount.add(relevance.getValue()).divide(BigDecimal.valueOf(weight), BigDecimal.ROUND_HALF_UP);
    }

    public void cancelRelevance(Relevance relevance)  {
        BigDecimal amount = this.mean.multiply(BigDecimal.valueOf(weight));
        weight--;
        this.mean = amount.subtract(relevance.getValue()).divide(BigDecimal.valueOf(weight), BigDecimal.ROUND_HALF_UP);
    }
}
