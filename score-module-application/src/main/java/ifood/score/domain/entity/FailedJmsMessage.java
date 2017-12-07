package ifood.score.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FailedJmsMessage {

    @Id
    private String id;

    private String destination;

    private String message;

    private String originalClass;

    public FailedJmsMessage(String destination, String message, String originalClass) {
        this.destination =  destination;
        this.message = message;
        this.originalClass = originalClass;
    }
}
