package uz.result.intermediatebot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.result.intermediatebot.bot.IntermediateBot;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class BotController {

    private final IntermediateBot intermediateBot;

    @PostMapping
    public void onUpdateReceived(
            @RequestBody Update update
    ) {
        intermediateBot.onWebhookUpdateReceived(update);
    }

}
