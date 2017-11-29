package ifood.score.domain.utils;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.entity.MenuScore;
import ifood.score.domain.entity.Relevance;
import ifood.score.domain.entity.Score;
import ifood.score.domain.exception.ScoreBuildingException;
import ifood.score.menu.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ScoreBuilder {

    private List<Relevance> relevances = new ArrayList<>();

    public ScoreBuilder withRelevance(Relevance relevance) {
        this.relevances.add(relevance);
        return this;
    }

    public <T extends Score> T build() throws ScoreBuildingException {
        validate();

        BigDecimal sum = relevances.stream().map(Relevance::getValue).reduce((v, acc) -> acc = acc.add(v)).get();
        BigDecimal scoreVal = sum.divide(BigDecimal.valueOf(relevances.size()), BigDecimal.ROUND_HALF_UP);

        Score score;
        Category category = relevances.get(0).getCategory();
        if(category != null) {
            score = new CategoryScore(category);
        } else {
            score = new MenuScore(relevances.get(0).getMenuId());
        }
        score.setMean(scoreVal);
        score.setWeight(relevances.size());

        return (T) score;
    }

    private void validate() throws ScoreBuildingException {
        if(relevances.size() == 0) {
            throw new ScoreBuildingException("Impossible to build Score with no relevance.");
        } else if (relevances.size() > 1){
            long categories = relevances.stream().map(Relevance::getCategory).distinct().count();
            long menus = relevances.stream().map(Relevance::getMenuId).distinct().count();
            long sum = categories + menus;
            if(sum > 1) {
                throw new ScoreBuildingException("Relevance with more than 1 category or menu. The count was " + sum);
            }
        }
    }
}
