package ifood.score.order;

import ifood.score.jms.JmsBridge;
import ifood.score.mock.generator.order.OrderPicker;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@Profile("benchmarking")
public class OrderCheckoutBenchmarking {

    private static final Logger logger = LoggerFactory.getLogger(OrderCheckoutBenchmarking.class);
    private static final int CHECKOUT_MESSAGES = 1_0;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JmsBridge jmsBridge;

    @Value("${score.order.checkout.queue-name}")
    private String checkoutOrderQueue;

    @Value("${score.order.cancel.queue-name}")
    private String cancelOrderQueue;

    private static OrderPicker picker = new OrderPicker();

    private Collection<UUID> cancellantionQueue = new ArrayList<>();

    @Scheduled(fixedDelay = 60 * 1000L)
    public void benchmark() throws InterruptedException {
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

//        SpringApplication.exit(applicationContext, () -> 2);
    }

    public Integer messageCount(String queueName) {
        return jmsBridge.getJmsTemplate().browse(queueName,
                (session, queueBrowser) -> Collections.list(queueBrowser.getEnumeration()).size());
    }
}
