package ifood.score.domain.entity;

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
        this.mean = amount.add(item.getValue()).divide(BigDecimal.valueOf(weight), BigDecimal.ROUND_HALF_EVEN);
    }

    public void cancel(Relevance.Item item)  {
        BigDecimal amount = this.mean.multiply(BigDecimal.valueOf(weight));;
        if(--weight > 0) {
            this.mean = amount.subtract(item.getValue()).divide(BigDecimal.valueOf(weight), BigDecimal.ROUND_HALF_EVEN);
        } else {
            this.mean = BigDecimal.ZERO;
        }
    }
}
