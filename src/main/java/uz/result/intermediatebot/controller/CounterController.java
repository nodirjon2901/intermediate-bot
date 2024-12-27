package uz.result.intermediatebot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.result.intermediatebot.model.ApiResponse;
import uz.result.intermediatebot.model.Button;
import uz.result.intermediatebot.service.CounterService;

@RestController
@RequestMapping("/v1/counter")
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addCallNum(
            @RequestParam(value = "button") Button button
    ) {
        return counterService.addCallNumber(button);
    }

}
