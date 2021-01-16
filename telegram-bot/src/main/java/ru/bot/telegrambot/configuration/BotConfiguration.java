package ru.bot.telegrambot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
    public BotService botService(BotProperties botProperties, ExtendedMessageInfoConverter converter) {
        return new BotService(botProperties, converter);
    }
}
