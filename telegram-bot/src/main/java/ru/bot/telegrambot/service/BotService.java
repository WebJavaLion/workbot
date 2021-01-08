package ru.bot.telegrambot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.bot.telegrambot.configuration.BotProperties;

/**
 * @author Lshilov
 */

@Service
public class BotService extends TelegramLongPollingBot {

    private final BotProperties botProperties;

    public BotService(BotProperties botProperties) {
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

    }
}
