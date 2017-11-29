package ifood.score.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MenuScore extends Score {

    @Id
    private UUID menuId;
}
