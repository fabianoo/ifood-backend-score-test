package ifood.score.test.integration;

import com.mongodb.MongoClient;
import ifood.score.jms.JmsBridge;
import ifood.score.mock.generator.order.OrderPicker;
import ifood.score.order.Order;
import ifood.score.service.ScoresRectifier;
import ifood.score.test.integration.helper.RelevanceReduceChecker;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static ifood.score.domain.utils.MathOperations.asBigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderCheckoutAccuracyTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderCheckoutAccuracyTest.class);

    private static final int CHECKOUT_MESSAGES = 10_000;

    @Autowired
    private JmsBridge jmsBridge;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private RelevanceReduceChecker checker;

    @Value("${score.order.checkout.queue-name}")
    private String checkoutOrderQueue;

    @Value("${score.order.cancel.queue-name}")
    private String cancelOrderQueue;

    @Value("${spring.data.mongodb.database}")
    private String database;

    private static OrderPicker picker = new OrderPicker();

    private Collection<UUID> cancellantionQueue = new ArrayList<>();

    @Autowired
    private ScoresRectifier scoresRectifier;

    @Before
    public void before() {
        mongoClient.dropDatabase(database);
    }

    @Test
    public void testAccuracy() throws InterruptedException {
        DateTime start = DateTime.now();

        IntStream.range(1, CHECKOUT_MESSAGES).forEach(i -> {
            Order order = picker.pick();
            if(i % 10 == 0){
                cancellantionQueue.add(order.getUuid());
            }
            jmsBridge.sendMessage(checkoutOrderQueue, order);
        });

        cancellantionQueue.forEach(c -> jmsBridge.sendMessage(cancelOrderQueue, c));

        Integer totalMessages;

        do {
            totalMessages = messageCount(cancelOrderQueue) + messageCount(checkoutOrderQueue);
            logger.info("Total Messages to Process: " + totalMessages);
            Thread.sleep(2 * 1000L);
        } while (totalMessages > 0);

        Duration duration = new Duration(start, DateTime.now());
        PeriodFormatter hm = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .appendSecondsWithMillis()
                .toFormatter();
        String result = hm.print(duration.toPeriod());

        logger.info("Order Checkout Messages: " + CHECKOUT_MESSAGES);
        logger.info("Order Cancel Messages: " + CHECKOUT_MESSAGES / 10);
        logger.info("It took " + result +  " to process everything.");

        scoresRectifier.rectify();

        logger.info("Now, checking the results...");

        List<BigDecimal> bigDecimals = checker.approximationIndexes();
        long outOffAccuracyRange = bigDecimals.stream().peek(b -> logger.info(String.format("Index: %s", b)))
                .filter(this::isInaccurate).count();

        logger.info(String.format(
                "There are a total %s of %s scores that are not accurate.", outOffAccuracyRange, bigDecimals.size()));

        Assert.assertEquals(0L, outOffAccuracyRange);
    }

    private Boolean isInaccurate(BigDecimal value) {
        BigDecimal minIndex = asBigDecimal(1).subtract(asBigDecimal(0.01));
        BigDecimal maxIndex = asBigDecimal(1).add(asBigDecimal(0.05));
        return value.compareTo(minIndex) < 0 || value.compareTo(maxIndex) > 0;
    }

    private Integer messageCount(String queueName) {
        return jmsBridge.getJmsTemplate().browse(queueName,
                (session, queueBrowser) -> Collections.list(queueBrowser.getEnumeration()).size());
    }
}
