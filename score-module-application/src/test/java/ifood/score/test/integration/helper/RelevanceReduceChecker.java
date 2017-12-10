package ifood.score.test.integration.helper;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.entity.MenuScore;
import ifood.score.domain.entity.Relevance;
import ifood.score.domain.entity.RelevanceStatus;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.domain.repository.MenuScoreRepository;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.domain.utils.MathOperations;
import ifood.score.domain.utils.ScoreBuilder;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RelevanceReduceChecker {

    @Autowired
    private RelevanceRepository relevanceRepository;

    @Autowired
    private CategoryScoreRepository categoryScoreRepository;

    @Autowired
    private MenuScoreRepository menuScoreRepository;

    public List<BigDecimal> approximationIndexes() {
        List<Relevance> relevances = relevanceRepository.findByStatus(RelevanceStatus.COMPUTED);

        Map<Category, List<Relevance.Item>> categoryMap = relevances.stream()
                .map(Relevance::getItems)
                .flatMap(Collection::stream)
                .filter(i -> i.getCategory() != null)
                .collect(Collectors.groupingBy(Relevance.Item::getCategory));

        List<CategoryScore> categoryScores = categoryScoreRepository.findAll();

        List<BigDecimal> categIndexes = categoryMap.keySet().stream().map(c -> {
            List<Relevance.Item> items = categoryMap.get(c);
            return (CategoryScore) new ScoreBuilder().withItems(items).build();
        }).map(s -> {
            Optional<CategoryScore> categScore = categoryScores.stream()
                    .filter(cs -> cs.getCategory().equals(s.getCategory())).findFirst();
            if (categScore.isPresent()) {
                return MathOperations.divide(s.getMean(), categScore.get().getMean());
            } else {
                return MathOperations.asBigDecimal(0);
            }
        }).collect(Collectors.toList());

        Map<UUID, List<Relevance.Item>> menuMap = relevances.stream().map(Relevance::getItems)
                .flatMap(Collection::stream)
                .filter(i -> i.getMenuId() != null)
                .collect(Collectors.groupingBy(Relevance.Item::getMenuId));

        List<MenuScore> menuScores = menuScoreRepository.findAll();

        List<BigDecimal> menuIndexes = menuMap.keySet().stream().map(m -> {
            List<Relevance.Item> items = menuMap.get(m);
            return (MenuScore) new ScoreBuilder().withItems(items).build();
        }).map(s -> {
            Optional<MenuScore> menuScore = menuScores.stream()
                    .filter(cs -> cs.getMenuId().equals(s.getMenuId())).findFirst();
            if (menuScore.isPresent()) {
                return MathOperations.divide(s.getMean(), menuScore.get().getMean());
            } else {
                return MathOperations.asBigDecimal(0);
            }
        }).collect(Collectors.toList());

        categIndexes.addAll(menuIndexes);

        return categIndexes;
    }
}
