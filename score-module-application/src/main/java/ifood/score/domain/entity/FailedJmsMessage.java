package ifood.score.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FailedJmsMessage {

    private String destination;

    private String message;
}
