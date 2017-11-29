package ifood.score.domain.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ScoreBuildingException extends BusinessException {

    public ScoreBuildingException(String message) {
        super(message);
    }
}
