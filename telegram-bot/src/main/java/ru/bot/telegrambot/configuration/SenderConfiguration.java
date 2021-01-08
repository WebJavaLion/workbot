package ru.bot.telegrambot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.converter.Converter;

import java.util.function.Consumer;

@Configuration
public class SenderConfiguration {

    @Bean
    public Consumer<ExtendedMessageInfo> sender(TelegramLongPollingBot bot, Converter<SendMessage, ExtendedMessageInfo> converter) {
        return m -> {
            try {
                bot.execute(converter.convert(m));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }
}
