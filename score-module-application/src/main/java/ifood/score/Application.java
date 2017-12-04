package ifood.score;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan
@EnableJms
@EnableAsync
@EnableScheduling
@EnableMongoRepositories
@EnableReactiveMongoRepositories
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class);
	}
}
