package ifood.score.jms;

import com.google.gson.Gson;
import ifood.score.domain.entity.FailedJmsMessage;
import ifood.score.domain.repository.FailedJmsMessageRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JmsBridge {

    private static final Logger logger = LoggerFactory.getLogger(JmsBridge.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private FailedJmsMessageRepository failedJmsMessageRepository;

    private Boolean healthy = true;

    private DateTime lastTry = DateTime.now();

    public void sendMessage(String destination, Object message) {
        String textMessage = new Gson().toJson(message);
        if(healthy || lastTry.isBefore(aMinuteAgo())) {
            lastTry = DateTime.now();
            try {
                jmsTemplate.convertAndSend(destination, textMessage);
                if(!healthy) {
                    healthy = true;
                    recoverFailedMessages();
                }
            } catch (JmsException ex) {
                logger.error("Failed to send JMS message. Caused by: " + ex.getMessage());
                healthy = false;
                fallBackToDatabase(destination, textMessage);
            }
        } else {
            fallBackToDatabase(destination, textMessage);
        }
    }

    private void recoverFailedMessages() {
        List<FailedJmsMessage> messages = failedJmsMessageRepository.findAll();
        messages.forEach(this::sendFailedMessage);
    }

    private void sendFailedMessage(FailedJmsMessage message) {
        this.sendMessage(message.getDestination(), message.getMessage());
        failedJmsMessageRepository.delete(message);
    }

    private DateTime aMinuteAgo() {
        return DateTime.now().minusMinutes(1);
    }

    private void fallBackToDatabase(String destination, String message) {
        FailedJmsMessage failedMessage = new FailedJmsMessage(destination, message);
        failedJmsMessageRepository.save(failedMessage);
    }
}
