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
//        if(relevanceRepository.countByOrderIdAndStatus(orderId, RelevanceStatus.NOT_COMPUTED) > 0) {
//            throw new ScoreAsyncComputingException(
//                    "Should not process cancellation. This order is not ready yeat. Retry later.");
//        }
//
//        List<Relevance> relevances = relevanceRepository.findByOrderIdAndStatus(orderId, RelevanceStatus.COMPUTED);
//
//        Map<UUID, List<Relevance>> menuMap = relevances.stream().filter(r -> r.getMenuId() != null)
//                .collect(Collectors.groupingBy(Relevance::getMenuId));
//        menuScoreRepository.findAllById(menuMap.keySet()).forEach(s -> {
//            menuMap.get(s.getMenuId()).forEach(s::cancel);
//            menuScoreRepository.save(s);
//        });
//
//        Map<Category, List<Relevance>> categMap = relevances.stream().filter(r -> r.getCategory() != null)
//                .collect(Collectors.groupingBy(Relevance::getCategory));
//        categoryScoreRepository.findAllById(categMap.keySet()).forEach(s -> {
//            categMap.get(s.getCategory()).forEach(s::cancel);
//            categoryScoreRepository.save(s);
//        });
//
//        relevances.forEach(s -> s.setStatus(RelevanceStatus.CANCELLED));
//        relevanceRepository.saveAll(relevances);
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
