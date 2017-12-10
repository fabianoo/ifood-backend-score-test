package ifood.score.test.integration;

import com.mongodb.MongoClient;
import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.jms.JmsBridge;
import ifood.score.menu.Category;
import ifood.score.order.Order;
import ifood.score.service.ScoreService;
import ifood.score.test.integration.setup.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static ifood.score.domain.utils.MathOperations.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ScoreCalculationTest {

    @Autowired
    private CategoryScoreRepository categoryScoreRepository;

    @Autowired
    private JmsBridge jmsBridge;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MongoClient mongoClient;

    @Value("${score.order.checkout.queue-name}")
    private String checkoutOrderQueue;

    @Value("${score.order.cancel.queue-name}")
    private String cancelOrderQueue;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Before
    public void before() throws InterruptedException {
        mongoClient.dropDatabase(database);

        Order order = orderRepository.onePizzaOrder();

        jmsBridge.sendMessage(checkoutOrderQueue, order);

        Thread.sleep(10 * 1000L);
    }

    @Test
    public void score() throws Exception {
        Optional<CategoryScore> score = categoryScoreRepository.findById(Category.PIZZA);

        assertTrue(score.isPresent());
        assertEquals(Integer.valueOf(1), score.get().getWeight());
        assertEquals(asBigDecimal(100), score.get().getMean());

    }
}