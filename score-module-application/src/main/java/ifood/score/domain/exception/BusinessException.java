package ifood.score.domain.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
class BusinessException extends RuntimeException {

    BusinessException(String message) {
        super(message);
    }

    BusinessException(String message, Throwable t) {
        super(t);
    }
}
