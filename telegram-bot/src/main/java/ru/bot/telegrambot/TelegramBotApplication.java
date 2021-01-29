package ru.bot.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.bot.telegrambot.configuration.BotProperties;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.processor.Processor;
import ru.bot.telegrambot.processor.ProcessorExtractor;
import ru.bot.telegrambot.service.BotService;

import java.util.Map;

/**
 * @author Lshilov
 */

@SpringBootApplication
@EnableConfigurationProperties(BotProperties.class)
public class TelegramBotApplication {

    public static void main(String[] args) throws NoSuchFieldException {
        ConfigurableApplicationContext context = SpringApplication.run(TelegramBotApplication.class, args);
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(context.getBean(BotService.class));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
