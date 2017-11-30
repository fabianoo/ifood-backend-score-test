package ifood.score.setup.converters;

import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.UUID;

public class StringToUuidConverter implements Converter<String, UUID> {

    @Override
    public UUID convert(String source) {
        return UUID.fromString(source);
    }
}