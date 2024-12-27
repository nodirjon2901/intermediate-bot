package uz.result.intermediatebot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.result.intermediatebot.model.Application;
import uz.result.intermediatebot.model.ApplicationByUser;
import uz.result.intermediatebot.model.Button;
import uz.result.intermediatebot.model.Counter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IntermediateBot extends TelegramWebhookBot {

    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    @Value("${bot.groupChatId}")
    private String groupChatId;

    @Value("${bot.webhook.path}")
    private String webhookPath;

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    public void handleSendApplication(Application application) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(groupChatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText(
                "*Новая заявка*\n\n" +
                        "\uD83D\uDC64 *ФИО*: " + application.getName() + "\n" +
                        "\uD83D\uDCDE *Электронная почта*: " + application.getEmail() + "\n" +
                        "\uD83D\uDCAC *Номер телефона*: " + application.getPhoneNumber() + "\n"
        );
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void handleSendApplicationByUser(ApplicationByUser application) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(groupChatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText(
                "*Новая заявка*\n\n" +
                        "\uD83D\uDC64 *ФИО*: " + application.getName() + "\n" +
                        "\uD83D\uDCAC *Номер телефона*: " + application.getPhoneNumber() + "\n" +
                        "\uD83D\uDD16 *Услуга*: " + application.getService() + "\n" +
                        "\uD83D\uDCAC *Комментарий*: " + application.getComment() + "\n"
        );
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendCounter(List<Counter> counters, Long totalApplications) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(groupChatId);
        StringBuilder textBuilder = new StringBuilder();

        textBuilder.append("<b>Еженедельный отчет \uD83D\uDCCB</b>\n\n");

        if ((counters == null || counters.isEmpty() && totalApplications == 0)) {
            textBuilder.append("<b>Поступившие заявки:</b> 0\n");
        } else {
            Map<Button, Long> buttonCountMap = new HashMap<>();
            long totalCalls = 0;
            long totalAppointments = 0;

            for (Counter counter : counters) {
                Button button = counter.getSection();
                long countCall = counter.getCountCall() != null ? counter.getCountCall() : 0;
                buttonCountMap.put(button, buttonCountMap.getOrDefault(button, 0L) + countCall);
                totalCalls += countCall;
            }

            for (Map.Entry<Button, Long> entry : buttonCountMap.entrySet()) {
                textBuilder.append(String.format("<b>%s: </b> %d\n", getButtonDisplayName(entry.getKey()), entry.getValue()));
            }
            textBuilder.append(String.format("\n<b>Общее количество заявок:</b> %d\n", totalApplications));
        }
        String text = textBuilder.toString();
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getButtonDisplayName(Button button) {
        return switch (button) {
            case CALL_HEADER -> "Phone icon (Header)";
            case TELEGRAM_FOOTER -> "Telegram (Footer)";
            case INSTAGRAM_FOOTER -> "Instagram (Footer)";
            case YOUTUBE_FOOTER -> "YouTube (Footer)";
            default -> button.name();
        };
    }
}
