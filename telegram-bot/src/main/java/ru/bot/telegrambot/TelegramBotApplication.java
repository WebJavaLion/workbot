package ru.bot.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
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
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class TelegramBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TelegramBotApplication.class, args);
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(context.getBean(BotService.class));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        final TestService testService = context.getBean(TestService.class);

        log.info(testService.addNewVacancies());
    }

    @FeignClient("vkstealer")
    static interface TestService{
        @GetMapping("/addvacancies")
        public String addNewVacancies();
    }
}
