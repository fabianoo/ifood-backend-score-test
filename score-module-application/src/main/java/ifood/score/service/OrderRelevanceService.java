package ifood.score.service;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.domain.repository.MenuScoreRepository;
import ifood.score.menu.Category;
import ifood.score.order.Order;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderRelevanceService {

    @Autowired
    private MenuScoreRepository menuScoreRepository;

    @Autowired
    private CategoryScoreRepository categoryScoreRepository;

    @Autowired
    private MongoClient mongoClient;

    public void processOrderCheckout(Order order) {
        OrderRelevanceComputer processor = new OrderRelevanceComputer(order);
        menuScoreRepository.saveAll(processor.menuRelevances());
        categoryScoreRepository.saveAll(processor.categoryRelevances());
    }

    @Deprecated
    public BigDecimal retrieveCategoryScoreMean(Category category) {
        List<BigDecimal> allScores = new ArrayList<>();
        Bson bson = Document.parse(String.format("{category: '%s'}", category.toString()));

        Block<BigDecimal> block = b -> allScores.add(b);
        mongoClient.getDatabase("test").getCollection("categoryScore")
                .find(bson)
                .map(i -> new BigDecimal(i.get("score").toString())).forEach(block);
        Optional<BigDecimal> score = allScores.stream().reduce((s, accumulator) -> accumulator.add(s));
        return score.get().divide(BigDecimal.valueOf(allScores.size()), BigDecimal.ROUND_HALF_UP);
    }
}
