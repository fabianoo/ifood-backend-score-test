package ifood.score.web;

import com.google.gson.JsonParser;
import ifood.score.domain.repository.RelevanceRepository;
import ifood.score.domain.utils.MathOperations;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.xml.bind.DatatypeConverter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BenchmarkingRestApi {

    @Autowired
    private RelevanceRepository relevanceRepository;

    @Value("${score.activemq.messages-dequeued.url}")
    private String messagesDequeuedUrl;

    /**
     * It returns the application uptime, messages consumed and an average messages processed per second
     *  TODO: This needs to be improved, once it considers all messages from the broker (even if the app is restarted)
     */
    @RequestMapping("/score/benchmarking")
    public Map<String, String> get() {
        Map<String, String> map = new LinkedHashMap<>();

        Long uptime = getSystemUptime();

        BigDecimal messagesProcessed = getMessagesProcessed();


        map.put("uptime", formatUptime(uptime));
        map.put("messages", messagesProcessed.setScale(0).toString());
        map.put("processing-rate", processingRate(messagesProcessed, uptime));

        return map;
    }

    public BigDecimal getMessagesProcessed() {
        String basicToken = DatatypeConverter.printBase64Binary("admin:admin".getBytes());

        WebClient.Builder webClientBuilder = WebClient.builder();
        webClientBuilder.baseUrl(messagesDequeuedUrl);
        webClientBuilder.defaultHeader("Authorization", "Basic " + basicToken);

        WebClient webClient = webClientBuilder.build();
        String response = webClient.get().retrieve().bodyToMono(String.class).block();

        return new JsonParser().parse(response).getAsJsonObject().get("value").getAsBigDecimal();
    }

    private Long getSystemUptime() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getUptime();
    }

    private String formatUptime(Long uptime) {
        Period period = Duration.millis(uptime).toPeriod();
        PeriodFormatter hm = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .appendSecondsWithMillis()
                .toFormatter();
        return hm.print(period);
    }

    private String processingRate(BigDecimal messagesCount, Long uptime) {
        uptime = uptime / 1_000L;
        BigDecimal rate = MathOperations.divide(messagesCount, uptime.intValue());
        return String.format("~ %s messages/s", rate.setScale(1, BigDecimal.ROUND_HALF_EVEN));
    }
}
