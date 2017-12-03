package ifood.score.domain.exception;

import org.springframework.jms.JmsException;

public class ScoreAsyncComputingException extends JmsException {
    public ScoreAsyncComputingException(String msg) {
        super(msg);
    }
}
