package ru.bot.telegrambot.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "bot")
@Getter
public final class BotProperties {

    private final String botUsername;
    private final String botToken;

    public BotProperties(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }
}
