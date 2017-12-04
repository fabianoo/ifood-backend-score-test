package ifood.score.setup;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import ifood.score.setup.converters.BigDecimalToDoubleConverter;
import ifood.score.setup.converters.DoubleToBigDecimalConverter;
import ifood.score.setup.converters.StringToUuidConverter;
import ifood.score.setup.converters.UuidToStringConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

//    @Value("${spring.data.mongodb.uri}")
//    private String connUri;

    @Override
    protected String getDatabaseName() {
        return "score";
    }

    @Override
    public MongoClient reactiveMongoClient() {
//        return MongoClients.create(connUri);
        return MongoClients.create();
    }

    @Override
    public CustomConversions customConversions() {
        return new CustomConversions(CustomConversions.StoreConversions.NONE,
                converters());
    }

    private List<Converter> converters() {
        return Arrays.asList(
                new UuidToStringConverter(),
                new StringToUuidConverter(),
                new DoubleToBigDecimalConverter(),
                new BigDecimalToDoubleConverter());
    }
}


