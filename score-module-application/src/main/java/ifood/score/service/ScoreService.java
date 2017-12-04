package ifood.score.service;

import ifood.score.domain.entity.*;
import ifood.score.domain.exception.ScoreAsyncComputingException;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.domain.repository.MenuScoreRepository;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.domain.utils.ScoreBuilder;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.management.JMException;
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
        if(relevanceRepository.countByOrderIdAndStatus(orderId, RelevanceStatus.NOT_COMPUTED) > 0) {
            throw new ScoreAsyncComputingException(
                    "Should not process cancellation. This order is not ready yeat. Retry later.");
        }

        List<Relevance> relevances = relevanceRepository.findByOrderIdAndStatus(orderId, RelevanceStatus.COMPUTED);

        Map<UUID, List<Relevance>> menuMap = relevances.stream().filter(r -> r.getMenuId() != null)
                .collect(Collectors.groupingBy(Relevance::getMenuId));
        menuScoreRepository.findAllById(menuMap.keySet()).forEach(s -> {
            menuMap.get(s.getMenuId()).forEach(s::cancelRelevance);
            menuScoreRepository.save(s);
        });

        Map<Category, List<Relevance>> categMap = relevances.stream().filter(r -> r.getCategory() != null)
                .collect(Collectors.groupingBy(Relevance::getCategory));
        categoryScoreRepository.findAllById(categMap.keySet()).forEach(s -> {
            categMap.get(s.getCategory()).forEach(s::cancelRelevance);
            categoryScoreRepository.save(s);
        });

        relevances.forEach(s -> s.setStatus(RelevanceStatus.CANCELLED));
        relevanceRepository.saveAll(relevances);
    }

    public void computeRelevance(Relevance relevance) {
        Optional<? extends Score> optional = scoreByRelevance(relevance);
        Score score;
        if(optional.isPresent()) {
            score = optional.get();
            score.computeRelevance(relevance);
        } else {
            score = new ScoreBuilder().withRelevance(relevance).build();
        }
        this.save(score);

        relevance.setStatus(RelevanceStatus.COMPUTED);
        relevanceRepository.save(relevance);
    }

    private void save(Score score) {
        if(score instanceof  CategoryScore) {
            categoryScoreRepository.save((CategoryScore) score);
        } else {
            menuScoreRepository.save((MenuScore) score);
        }
    }

    private Optional<? extends Score> scoreByRelevance(Relevance relevance) {
        Category category = relevance.getCategory();

        if (category != null) return categoryScoreRepository.findById(category);
        else return menuScoreRepository.findById(relevance.getMenuId());
    }
}
