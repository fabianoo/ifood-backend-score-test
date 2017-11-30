package ifood.score.setup;

import com.mongodb.MongoClient;
import ifood.score.setup.converters.BigDecimalToDoubleConverter;
import ifood.score.setup.converters.DoubleToBigDecimalConverter;
import ifood.score.setup.converters.StringToUuidConverter;
import ifood.score.setup.converters.UuidToStringConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongo(), mongoDatabase);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.setCustomConversions(customConversions());
        mongoMapping.afterPropertiesSet();
        return mongoTemplate;

    }

    @Bean
    public MongoClient mongo() throws Exception {
        return new MongoClient(mongoHost, mongoPort);
    }


    private CustomConversions customConversions() {
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


