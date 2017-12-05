package ifood.score.web;

import ifood.score.domain.entity.CategoryScore;
import ifood.score.domain.entity.MenuScore;
import ifood.score.domain.repository.ReactiveCategoryScoreRepository;
import ifood.score.domain.repository.ReactiveMenuScoreRepository;
import ifood.score.menu.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ScoreRestApi {

    @Autowired
    private ReactiveCategoryScoreRepository categoryScoreRepository;

    @Autowired
    private ReactiveMenuScoreRepository menuScoreRepository;

    @RequestMapping("/categories/{category}/score")
    public Mono<CategoryScore> categoryScore(@PathVariable String category) {
        Category cat = Category.valueOf(category.toUpperCase());
        return categoryScoreRepository.findById(cat);
    }

    @RequestMapping("/menu/{menu}/score")
    public Mono<MenuScore> menuScore(@PathVariable UUID menu) {
        return menuScoreRepository.findById(menu);
    }

    @RequestMapping("/categories/scores")
    public Flux<CategoryScore> categoriesScores(
            @RequestParam(value = "min-score", defaultValue = "0") BigDecimal minScore,
            @RequestParam(value = "max-score", defaultValue = "100") BigDecimal maxScore) {

        return categoryScoreRepository.findByMeanBetweenOrderByMeanAsc(minScore, maxScore);
    }

    @RequestMapping("/menu/scores")
    public Flux<MenuScore> menuScores(
            @RequestParam(value = "min-score", defaultValue = "0") BigDecimal minScore,
            @RequestParam(value = "max-score", defaultValue = "100") BigDecimal maxScore,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "100") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        return menuScoreRepository.findByMeanBetweenOrderByMeanAsc(minScore, maxScore, pageable);
    }
}
