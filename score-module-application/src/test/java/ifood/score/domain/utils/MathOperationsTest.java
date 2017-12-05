package ifood.score.domain.utils;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class MathOperationsTest {

    @Test
    public void divide() {
        BigDecimal quotient = MathOperations.divide(BigDecimal.valueOf(1), BigDecimal.valueOf(1_000_000_000));
        Assert.assertEquals(BigDecimal.valueOf(0.000_000_001), quotient);

        quotient = MathOperations.divide(BigDecimal.valueOf(1), BigDecimal.valueOf(4_000_000_000L));
        Assert.assertEquals(BigDecimal.valueOf(0.000_000_000_2).setScale(10), quotient);
    }

    @Test
    public void sqrt() {
        BigDecimal sqrt = MathOperations.sqrt(BigDecimal.valueOf(9));
        Assert.assertEquals(BigDecimal.valueOf(3), sqrt.setScale(0));

        sqrt = MathOperations.sqrt(BigDecimal.valueOf(64));
        Assert.assertEquals(BigDecimal.valueOf(8), sqrt.setScale(0));
    }
}