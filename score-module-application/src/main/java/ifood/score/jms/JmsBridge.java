package ifood.score.jms;

import com.google.gson.Gson;
import ifood.score.domain.entity.FailedJmsMessage;
import ifood.score.domain.repository.FailedJmsMessageRepository;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Component
public class JmsBridge {

    private static final Logger logger = LoggerFactory.getLogger(JmsBridge.class);

    @Autowired
    @Getter
    private JmsTemplate jmsTemplate;

    @Autowired
    private FailedJmsMessageRepository failedJmsMessageRepository;

    private Boolean healthy = true;

    private LocalDateTime lastTry = LocalDateTime.now();

    public void sendMessage(String destination, Object message) {
        String textMessage;
        if(message instanceof UUID) {
            textMessage = message.toString();
        } else {
            textMessage = new Gson().toJson(message);
        }

        if(healthy || lastTry.isBefore(aMinuteAgo())) {
            lastTry = LocalDateTime.now();
            try {
                jmsTemplate.convertAndSend(destination, textMessage);
                if(!healthy) {
                    healthy = true;
                    recoverFailedMessages();
                }
            } catch (JmsException ex) {
                logger.error("Failed to send JMS message. Caused by: " + ex.getMessage());
                healthy = false;
                fallBackToDatabase(destination, textMessage, message.getClass());
            }
        } else {
            fallBackToDatabase(destination, textMessage, message.getClass());
        }
    }

    private void recoverFailedMessages() {
        failedJmsMessageRepository.findAll().forEach(this::sendFailedMessage);
    }

    private void sendFailedMessage(FailedJmsMessage message) {
        try {
            Object objMsg = new Gson().fromJson(message.getMessage(), Class.forName(message.getOriginalClass()));
            this.sendMessage(message.getDestination(), objMsg);
            failedJmsMessageRepository.delete(message);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException("Could not recover message.", e);
        }
    }

    private LocalDateTime aMinuteAgo() {
        return LocalDateTime.now().minusMinutes(1);
    }

    private void fallBackToDatabase(String destination, String message, Class clazz) {
        FailedJmsMessage failedMessage = new FailedJmsMessage(destination, message, clazz.getCanonicalName());
        failedJmsMessageRepository.save(failedMessage);
    }
}
