package ifood.score.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Component
public class ScoresRectifier {

    private static final Logger logger = LoggerFactory.getLogger(ScoresRectifier.class);

    @Autowired
    private JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    @Autowired
    private ScoreService scoreService;

    @Value("${score.order.checkout.queue-name}")
    private String checkoutOrderQueue;

    @Value("${score.order.cancel.queue-name}")
    private String cancelOrderQueue;

    @Scheduled(cron = "0 5 * * * ?")
    public void rectify() {
        logger.info("Time to rectify all scores!");
        Set<String> listenerContainerIds = jmsListenerEndpointRegistry.getListenerContainerIds();
        logger.info("Stopping JMS Listeners...");
        listenerContainerIds.forEach(id -> jmsListenerEndpointRegistry.getListenerContainer(id).stop());

        logger.info("Recalculating all scores");
        Date start = new Date();
        scoreService.recalculateAllScores();
        Date ending = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        logger.info("It took " + sdf.format(new Date(ending.getTime() - start.getTime())) + " to recalculate.");

        logger.info("Restarting JMS Listeners...");
        listenerContainerIds.forEach(id -> jmsListenerEndpointRegistry.getListenerContainer(id).start());
    }
}
