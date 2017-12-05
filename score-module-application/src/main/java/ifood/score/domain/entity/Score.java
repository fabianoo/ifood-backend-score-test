package ifood.score.domain.entity;

import ifood.score.domain.utils.MathOperations;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public abstract class Score {

    private BigDecimal mean;

    private Integer weight;

    public void compute(Relevance.Item item) {
        BigDecimal amount = this.mean.multiply(BigDecimal.valueOf(weight));
        weight++;
        this.mean = MathOperations.divide(amount.add(item.getValue()), BigDecimal.valueOf(weight));
    }

    public void avoid(Relevance.Item item)  {
        BigDecimal amount = this.mean.multiply(BigDecimal.valueOf(weight));
        if(--weight > 0) {
            this.mean = MathOperations.divide(amount.subtract(item.getValue()), BigDecimal.valueOf(weight));
        } else {
            this.mean = BigDecimal.ZERO;
            this.weight = 0;
        }
    }
}
