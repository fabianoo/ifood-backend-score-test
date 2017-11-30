package ifood.score.web;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.entity.MenuScore;
import ifood.score.domain.entity.Score;
import ifood.score.domain.repository.CategoryScoreRepository;
import ifood.score.domain.repository.MenuScoreRepository;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ScoreRestApi {

    @Autowired
    private CategoryScoreRepository categoryScoreRepository;

    @Autowired
    private MenuScoreRepository menuScoreRepository;

    @RequestMapping("/categories/{category}/score")
    public BigDecimal categoryScoreMean(@PathVariable("category") String category) {
        Category cat = Category.valueOf(category.toUpperCase());
        Score score = categoryScoreRepository.findById(cat).get();
        return score.getMean();
    }

    @RequestMapping("/menu/{menu}/score")
    public BigDecimal menuScoreMean(@PathVariable("menu") UUID menu) {
        Score score = menuScoreRepository.findById(menu).get();
        return score.getMean();
    }

    @RequestMapping("/categories/scores")
    public Map<Category, BigDecimal> categoriesScores(
            @RequestParam(value = "min-score", defaultValue = "0") BigDecimal minScore,
            @RequestParam(value = "max-score", defaultValue = "100") BigDecimal maxScore) {

        Map<Category, BigDecimal> map = new LinkedHashMap<>();

        List<CategoryScore> scores = categoryScoreRepository.findByMeanBetweenOrderByMeanAsc(minScore, maxScore);
        scores.forEach(s -> map.put(s.getCategory(), s.getMean()));

        return map;
    }

    @RequestMapping("/menu/scores")
    public Map<UUID, BigDecimal> categoriesScores(
            @RequestParam(value = "min-score", defaultValue = "0") BigDecimal minScore,
            @RequestParam(value = "max-score", defaultValue = "100") BigDecimal maxScore,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "100") Integer size) {

        Map<UUID, BigDecimal> map = new LinkedHashMap<>();

        Pageable pageable = PageRequest.of(page, size);

        Page<MenuScore> scores = menuScoreRepository.findByMeanBetweenOrderByMeanAsc(minScore, maxScore, pageable);
        scores.forEach(s -> map.put(s.getMenuId(), s.getMean()));

        return map;
    }
}
