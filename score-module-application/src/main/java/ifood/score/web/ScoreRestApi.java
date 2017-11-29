package ifood.score.web;

import ifood.score.domain.entity.Score;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.domain.repository.MenuScoreRepository;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/score")
public class ScoreRestApi {

    @Autowired
    private CategoryScoreRepository categoryScoreRepository;

    @Autowired
    private MenuScoreRepository menuScoreRepository;

    @RequestMapping("/category/{category}")
    public BigDecimal categoryScoreMean(@PathVariable("category") String category) throws IOException {
        Category cat = Category.valueOf(category.toUpperCase());
        Score score = categoryScoreRepository.findById(cat).get();
        return score.getMean();
    }

    @RequestMapping("/menu/{menu}")
    public BigDecimal menuScoreMean(@PathVariable("menu") UUID menu) throws IOException {
        Score score = menuScoreRepository.findById(menu).get();
        return score.getMean();
    }
}
