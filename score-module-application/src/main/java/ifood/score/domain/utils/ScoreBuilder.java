package ifood.score.domain.utils;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.entity.MenuScore;
import ifood.score.domain.entity.Relevance;
import ifood.score.domain.entity.Score;
import ifood.score.domain.exception.ScoreBuildingException;
import ifood.score.menu.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ScoreBuilder {

    private List<Relevance.Item> relevanceItems = new ArrayList<>();

    public ScoreBuilder withItem(Relevance.Item item) {
        this.relevanceItems.add(item);
        return this;
    }

    public ScoreBuilder withItems(Collection<Relevance.Item> items) {
        this.relevanceItems.addAll(items);
        return this;
    }

    public <T extends Score> T build() throws ScoreBuildingException {
        validate();

        Score score;
        Category category = relevanceItems.get(0).getCategory();
        if(category != null) {
            score = new CategoryScore(category);
        } else {
            score = new MenuScore(relevanceItems.get(0).getMenuId());
        }

        BigDecimal sum = relevanceItems.stream().map(Relevance.Item::getValue)
                .reduce((v, acc) -> acc = acc.add(v)).get();
        BigDecimal scoreVal = MathOperations.divide(sum, relevanceItems.size());
        score.setMean(scoreVal);
        score.setWeight(relevanceItems.size());

        return (T) score;
    }

    private void validate() throws ScoreBuildingException {
        if(relevanceItems.size() == 0) {
            throw new ScoreBuildingException("Impossible to build Score with no relevance.");
        } else if (relevanceItems.size() > 1){
            long categories = relevanceItems.stream().map(Relevance.Item::getCategory)
                    .filter(Objects::nonNull).distinct().count();
            long menus = relevanceItems.stream().map(Relevance.Item::getMenuId)
                    .filter(Objects::nonNull).distinct().count();
            long sum = categories + menus;
            if(sum > 1) {
                throw new ScoreBuildingException("Relevance with more than 1 category or menu. The count was " + sum);
            }
        }
    }
}
