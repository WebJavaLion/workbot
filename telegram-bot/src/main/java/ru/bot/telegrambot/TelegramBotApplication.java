package ru.bot.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.bot.telegrambot.configuration.BotProperties;
import ru.bot.telegrambot.service.BotService;

/**
 * @author Lshilov
 */

@SpringBootApplication
@EnableConfigurationProperties(BotProperties.class)
@EnableEurekaClient
@EnableFeignClients
public class TelegramBotApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TelegramBotApplication.class, args);
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(context.getBean(BotService.class));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
