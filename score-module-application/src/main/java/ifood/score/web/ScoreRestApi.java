package ifood.score.web;

import ifood.score.menu.Category;
import ifood.score.service.OrderScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/score")
public class ScoreRestApi {

    @Autowired
    private OrderScoreService service;

    @RequestMapping("/category/{category}")
    public BigDecimal categoryScoreMean(@PathVariable("category") String category) throws IOException {
        Category cat = Category.valueOf(category.toUpperCase());
        return service.retrieveCategoryScoreMean(cat);
    }
}
