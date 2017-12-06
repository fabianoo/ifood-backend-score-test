package ifood.score.domain.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static ifood.score.domain.utils.MathOperations.*;


public class MathOperationsTest {

    @Test
    public void divideBigDecimals() {
        BigDecimal quotient = MathOperations.divide(BigDecimal.ONE, asBigDecimal(1_000_000_000));
        assertEquals(asBigDecimal(0.000_000_001), quotient);

        quotient = MathOperations.divide(BigDecimal.ONE, asBigDecimal(400_000_000));
        assertEquals(asBigDecimal(0.000_000_002_5), quotient);

        quotient = MathOperations.divide(BigDecimal.ZERO, asBigDecimal(400_000_000));
        assertEquals(asBigDecimal(0), quotient);
    }

    @Test
    public void divideIntegers() {
        BigDecimal quotient = MathOperations.divide(1, 3);
        assertEquals(asBigDecimal(0.333_333_333_333), quotient);

        quotient = MathOperations.divide(1, 10);
        assertEquals(asBigDecimal(0.1), quotient);

        quotient = MathOperations.divide(0, 12);
        assertEquals(asBigDecimal(0), quotient);
    }

    @Test
    public void divideBoth() {
        BigDecimal quotient = MathOperations.divide(BigDecimal.ONE, 3);
        assertEquals(asBigDecimal(0.333_333_333_333), quotient);

        quotient = MathOperations.divide(BigDecimal.ONE, 20);
        assertEquals(asBigDecimal(0.05), quotient);

        quotient = MathOperations.divide(BigDecimal.ZERO, 34);
        assertEquals(asBigDecimal(0), quotient);
    }

    @Test
    public void sqrt() {
        BigDecimal sqrt = MathOperations.sqrt(BigDecimal.valueOf(9));
        assertEquals(asBigDecimal(3), sqrt);

        sqrt = MathOperations.sqrt(asBigDecimal(64));
        assertEquals(asBigDecimal(8), sqrt);

        sqrt = MathOperations.sqrt(asBigDecimal(2));
        assertEquals(asBigDecimal(1.4142135624), sqrt);

        sqrt = MathOperations.sqrt(asBigDecimal(97));
        assertEquals(asBigDecimal(9.8488578018), sqrt);
    }
}