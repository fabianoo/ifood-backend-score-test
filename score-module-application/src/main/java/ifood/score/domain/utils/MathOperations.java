package ifood.score.domain.utils;

import java.math.BigDecimal;

public class MathOperations {

    private static final Integer DEFAULT_SCALE = 10;

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, DEFAULT_SCALE, BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal sqrt(BigDecimal radicand) {
        double sqrt = Math.sqrt(radicand.doubleValue());
        return BigDecimal.valueOf(sqrt);
    }
}
