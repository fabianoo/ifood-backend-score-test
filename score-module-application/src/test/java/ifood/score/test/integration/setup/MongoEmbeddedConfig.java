package ifood.score.test.integration.setup;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MongoEmbeddedConfig {

    @Bean
    public MongoClient mongoClient() throws IOException {
        MongodForTestsFactory factory = MongodForTestsFactory.with(Version.Main.V3_4);
        return factory.newMongo();
    }
}
