package ifood.score.service;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.entity.MenuScore;
import ifood.score.domain.entity.Relevance;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.domain.repository.MenuScoreRepository;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.domain.utils.ScoreBuilder;
import ifood.score.menu.Category;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AsyncRelevanceReduceChecker {

    @Autowired
    private RelevanceRepository relevanceRepository;

    @Autowired
    private CategoryScoreRepository categoryScoreRepository;

    @Autowired
    private MenuScoreRepository menuScoreRepository;

    @Scheduled(fixedDelay = 60 * 1000L)
    public void test() {
        DateTime start = DateTime.now();

        List<Relevance> relevances = relevanceRepository.findAll();

        Map<Category, List<Relevance.Item>> categoryMap = relevances.stream()
                .map(Relevance::getItems)
                .flatMap(Collection::stream)
                .filter(i -> i.getCategory() != null)
                .collect(Collectors.groupingBy(Relevance.Item::getCategory));

        List<CategoryScore> categoryScores = categoryMap.keySet().stream().map(c -> {
            List<Relevance.Item> items = categoryMap.get(c);
            return (CategoryScore) new ScoreBuilder().withItems(items).build();
        }).collect(Collectors.toList());

        categoryScoreRepository.saveAll(categoryScores);

        Map<UUID, List<Relevance.Item>> menuMap = relevances.stream().map(Relevance::getItems)
                .flatMap(Collection::stream)
                .filter(i -> i.getMenuId() != null)
                .collect(Collectors.groupingBy(Relevance.Item::getMenuId));

        List<MenuScore> menuScores = menuMap.keySet().stream().map(m -> {
            List<Relevance.Item> items = menuMap.get(m);
            return (MenuScore) new ScoreBuilder().withItems(items).build();
        }).collect(Collectors.toList());

        menuScoreRepository.saveAll(menuScores);

        Duration duration = new Duration(start, DateTime.now());
        PeriodFormatter hm = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .appendSecondsWithMillis()
                .toFormatter();
        String result = hm.print(duration.toPeriod());

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println(result);
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}
