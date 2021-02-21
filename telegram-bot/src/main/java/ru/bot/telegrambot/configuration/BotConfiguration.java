package ru.bot.telegrambot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.bot.telegrambot.pojo.converter.ExtendedMessageInfoConverter;
import ru.bot.telegrambot.service.BotService;

import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Configuration
public class BotConfiguration {

    @Bean
    public Consumer<SendMessage> sender(BotProperties botProperties, ExtendedMessageInfoConverter converter) {
        return m -> botService(botProperties, converter).executeSendMessage(m);
    }

    @Bean
    public Consumer<AnswerCallbackQuery> answerSender(BotProperties botProperties, ExtendedMessageInfoConverter converter) {
        return answer -> {
            try {
                botService(botProperties, converter).execute(answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    public BotService botService(BotProperties botProperties, ExtendedMessageInfoConverter converter) {
        return new BotService(botProperties, converter);
    }
}
