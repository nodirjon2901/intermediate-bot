package uz.result.intermediatebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.result.intermediatebot.bot.IntermediateBot;
import uz.result.intermediatebot.model.ApiResponse;
import uz.result.intermediatebot.model.Button;
import uz.result.intermediatebot.model.Counter;
import uz.result.intermediatebot.repository.ApplicationRepository;
import uz.result.intermediatebot.repository.CounterRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterRepository counterRepository;

    private final ApplicationRepository applicationRepository;

    private final IntermediateBot intermediateBot;

    public ResponseEntity<ApiResponse<?>> addCallNumber(Button button) {
        ApiResponse<?> response = new ApiResponse<>();
        Counter counter = Counter.builder()
                .section(button)
                .countCall(1L)
                .build();
        counterRepository.save(counter);
        response.setMessage("Success. Button " + button.name() + " count incremented");
        return ResponseEntity.status(201).body(response);
    }

//        @Scheduled(cron = "0 * * * * *")//every minute
    @Scheduled(cron = "0 0 0 * * MON",zone = "Asia/Tashkent")
    public void checkAndSendCounter() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldTime=now.minusWeeks(1);

        List<Counter> counterList = counterRepository.findAllByCreatedDateBetween(oldTime, now);

        Map<Button, Long> aggregatedCounters=new HashMap<>();
        for (Counter counter : counterList) {
            aggregatedCounters.put(
                    counter.getSection(),
                    aggregatedCounters.getOrDefault(counter.getSection(), 0L)+counter.getCountCall()
            );
        }

        List<Counter> savedCounters=new ArrayList<>();
        for (Map.Entry<Button, Long> entry : aggregatedCounters.entrySet()) {
            Counter aggregatedCounter = Counter.builder()
                    .section(entry.getKey())
                    .countCall(entry.getValue())
                    .build();
            savedCounters.add(aggregatedCounter);
        }

        counterRepository.deleteAll(counterList);
        Long applicationCount = applicationRepository.countApplicationInTheWeek(oldTime, now);
        intermediateBot.sendCounter(savedCounters,applicationCount);
    }

}