package ifood.score.setup.converters;

import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.UUID;

public class UuidToStringConverter implements Converter<UUID, String> {

    @Override
    public String convert(UUID source) {
        return source.toString();
    }
}