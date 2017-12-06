package ifood.score.domain.utils;

import java.math.BigDecimal;
import java.util.Arrays;

public class MathOperations {

    private static final Integer DEFAULT_SCALE = 10;
    private static final Integer ROUND_MODE = BigDecimal.ROUND_HALF_EVEN;

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, DEFAULT_SCALE, ROUND_MODE);
    }

    public static BigDecimal divide(BigDecimal dividend, Integer divisor) {
        return divide(dividend, asBigDecimal(divisor));
    }

    public static BigDecimal divide(Integer dividend, Integer divisor) {
        return divide(asBigDecimal(dividend), divisor);
    }

    public static BigDecimal multiply(Integer intFactor, BigDecimal... factors) {
        BigDecimal product = Arrays.stream(factors).reduce(BigDecimal.ONE, BigDecimal::multiply);
        return multiply(product, intFactor);
    }

    public static BigDecimal multiply(BigDecimal factor, Integer... intFactors) {
        Integer product = Arrays.stream(intFactors).reduce(1, (f1, f2) -> f1 * f2);
        return factor.multiply(asBigDecimal(product)).setScale(DEFAULT_SCALE, ROUND_MODE);
    }

    public static BigDecimal sqrt(BigDecimal radicand) {
        Double sqrt = Math.sqrt(radicand.doubleValue());
        return asBigDecimal(sqrt);
    }

    public static BigDecimal asBigDecimal(Double value) {
        return BigDecimal.valueOf(value).setScale(10, ROUND_MODE);
    }

    public static BigDecimal asBigDecimal(Integer value) {
        return BigDecimal.valueOf(value).setScale(10, ROUND_MODE);
    }
}
