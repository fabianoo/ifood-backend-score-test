package ifood.score.service;

import ifood.score.domain.entity.*;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.domain.repository.MenuScoreRepository;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.domain.utils.ScoreBuilder;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScoreService {

    @Autowired
    private CategoryScoreRepository categoryScoreRepository;

    @Autowired
    private MenuScoreRepository menuScoreRepository;

    @Autowired
    private RelevanceRepository relevanceRepository;

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
