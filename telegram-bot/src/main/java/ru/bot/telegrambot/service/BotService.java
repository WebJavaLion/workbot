package ru.bot.telegrambot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.bot.telegrambot.configuration.BotProperties;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.converter.Converter;

/**
 * @author Lshilov
 */

@Service
public class BotService extends TelegramLongPollingBot {

    private final Converter<ExtendedMessageInfo, Update> converter;
    private final BotProperties botProperties;

    public BotService(Converter<ExtendedMessageInfo, Update> converter, BotProperties botProperties) {
        this.converter = converter;
        this.botProperties = botProperties;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        ExtendedMessageInfo convert = converter.convert(update);
        System.out.println(convert);
    }
}
