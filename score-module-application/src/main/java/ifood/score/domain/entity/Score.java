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

    Score() {
        this.mean = BigDecimal.ZERO;
        this.weight = 0;
    }

    public void compute(Relevance.Item item) {
        BigDecimal amount = amount();
        weight++;
        this.mean = MathOperations.divide(amount.add(item.getValue()), weight);
    }

    public void avoid(Relevance.Item item)  {
        BigDecimal amount = amount();
        if(--weight > 0) {
            this.mean = MathOperations.divide(amount.subtract(item.getValue()), weight);
        } else {
            this.mean = BigDecimal.ZERO;
            this.weight = 0;
        }
    }

    private BigDecimal amount() {
        return MathOperations.multiply(this.mean, weight);
    }
}
