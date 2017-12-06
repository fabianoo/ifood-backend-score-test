package ifood.score.domain.entity;

import ifood.score.menu.Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;
import static ifood.score.domain.utils.MathOperations.*;

public class ScoreTest {

    private CategoryScore categoryScore;

    private MenuScore menuScore;

    private Relevance.Item firstItem;

    private Relevance.Item secondItem;

    private BigDecimal categInitialValue = asBigDecimal(44.444444444444);
    private BigDecimal menuInitialValue = asBigDecimal(51.2);


    @Before
    public void before() {
        firstItem = new Relevance.Item(null, null, asBigDecimal(50));
        secondItem = new Relevance.Item(null, null, asBigDecimal(11.11111111111111));

        categoryScore = new CategoryScore(Category.PIZZA);
        categoryScore.setMean(categInitialValue);
        categoryScore.setWeight(10);

        menuScore = new MenuScore(UUID.randomUUID());
        menuScore.setMean(menuInitialValue);
        menuScore.setWeight(2);
    }

    @Test
    public void computeAndAvoidCategoryScore() throws Exception {
        categoryScore.compute(secondItem);

        Assert.assertEquals(asBigDecimal(41.41414141414141414141414), categoryScore.getMean());

        categoryScore.avoid(secondItem);

        Assert.assertEquals(categInitialValue, categoryScore.getMean());
    }

    @Test
    public void computeAndAvoidMenuScore() {
        menuScore.compute(firstItem);

        Assert.assertEquals(asBigDecimal(50.8), menuScore.getMean());

        menuScore.avoid(firstItem);

        Assert.assertEquals(menuInitialValue, menuScore.getMean());
    }

    @Test
    public void avoidCategoryScore() throws Exception {
        categoryScore.compute(firstItem);

        Assert.assertEquals(asBigDecimal(44.949494949494), categoryScore.getMean());
    }

    @Test
    public void avoidMenuScore() throws Exception {
        menuScore.avoid(secondItem);

        Assert.assertEquals(asBigDecimal(91.288888888889), menuScore.getMean());
    }

}