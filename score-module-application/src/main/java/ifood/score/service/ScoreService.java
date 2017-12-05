package ifood.score.service;

import ifood.score.domain.entity.*;
import ifood.score.domain.exception.ScoreAsyncComputingException;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.domain.repository.MenuScoreRepository;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.domain.utils.ScoreBuilder;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    @Autowired
    private CategoryScoreRepository categoryScoreRepository;

    @Autowired
    private MenuScoreRepository menuScoreRepository;

    @Autowired
    private RelevanceRepository relevanceRepository;

    public void cancelRelevancesFromOrderId(String orderId) throws ScoreAsyncComputingException {
        Optional<Relevance> optional = relevanceRepository.findById(UUID.fromString(orderId));

        if(!optional.isPresent()) {
            throw new ScoreAsyncComputingException(String.format(
                    "Should not process cancellation. This Order(%s) is not ready yeat. Retrying later.",
                    orderId));
        }

        Relevance relevance = optional.get();

        if(relevance.isComputed()) {
            List<Relevance.Item> items = relevance.getItems();

            avoidItems(items);

            relevance.setStatus(RelevanceStatus.CANCELLED);
            relevanceRepository.save(relevance);
        }
    }

    public void avoidItems(List<Relevance.Item> items) {
        Map<UUID, List<Relevance.Item>> menuMap = items.stream()
                .filter(r -> r.getMenuId() != null)
                .collect(Collectors.groupingBy(Relevance.Item::getMenuId));

        menuScoreRepository.findAllById(menuMap.keySet()).forEach(s -> {
            menuMap.get(s.getMenuId()).forEach(s::avoid);
            menuScoreRepository.save(s);
        });

        Map<Category, List<Relevance.Item>> categMap = items.stream()
                .filter(r -> r.getCategory() != null)
                .collect(Collectors.groupingBy(Relevance.Item::getCategory));

        categoryScoreRepository.findAllById(categMap.keySet()).forEach(s -> {
            categMap.get(s.getCategory()).forEach(s::avoid);
            categoryScoreRepository.save(s);
        });
    }

    public void computeRelevance(Relevance.Item item) {
        Optional<? extends Score> optional = scoreByRelevance(item);
        Score score;
        if(optional.isPresent()) {
            score = optional.get();
            score.compute(item);
        } else {
            score = new ScoreBuilder().withItem(item).build();
        }
        this.save(score);
    }

    private void save(Score score) {
        if(score instanceof  CategoryScore) {
            categoryScoreRepository.save((CategoryScore) score);
        } else {
            menuScoreRepository.save((MenuScore) score);
        }
    }

    private Optional<? extends Score> scoreByRelevance(Relevance.Item item) {
        Category category = item.getCategory();

        if (category != null) return categoryScoreRepository.findById(category);
        else return menuScoreRepository.findById(item.getMenuId());
    }
}
