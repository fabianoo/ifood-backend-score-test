package ifood.score.service;

import ifood.score.domain.entity.Relevance;
import ifood.score.domain.entity.RelevanceStatus;
import ifood.score.domain.repository.RelevanceRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpiringScoresJob {

    @Value("${score.order.expiration-days}")
    private Integer expirationDays;

    @Autowired
    private RelevanceRepository relevanceRepository;

    @Autowired
    private ScoreService scoreService;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void expire() {
        Date date = DateTime.now().minusDays(expirationDays).toDate();
        List<Relevance> relevances = relevanceRepository
                .findByConfirmedAtBeforeAndStatus(date, RelevanceStatus.COMPUTED);

        List<Relevance.Item> items = relevances.stream().flatMap(r -> r.getItems().stream())
                .collect(Collectors.toList());
        scoreService.avoidItems(items);

        relevances.forEach(r -> r.setStatus(RelevanceStatus.EXPIRED));
        relevanceRepository.saveAll(relevances);
    }
}
