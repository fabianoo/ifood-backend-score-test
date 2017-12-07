package ifood.score.setup;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import ifood.score.setup.converters.BigDecimalToDoubleConverter;
import ifood.score.setup.converters.DoubleToBigDecimalConverter;
import ifood.score.setup.converters.StringToUuidConverter;
import ifood.score.setup.converters.UuidToStringConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private String mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(new ConnectionString(String.format("mongodb://%s:%s", mongoHost, mongoPort)));
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


