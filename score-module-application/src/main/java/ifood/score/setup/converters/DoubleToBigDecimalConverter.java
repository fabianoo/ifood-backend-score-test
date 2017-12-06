package ifood.score.setup.converters;

import ifood.score.domain.utils.MathOperations;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

public class DoubleToBigDecimalConverter implements Converter<Double, BigDecimal> {
 
    @Override
    public BigDecimal convert(Double source) {
        return MathOperations.asBigDecimal(source);
    }
}