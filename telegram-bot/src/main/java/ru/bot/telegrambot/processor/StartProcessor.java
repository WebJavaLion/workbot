package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
public class StartProcessor implements Processor{

    private final UserInfoRepository repository;
    private final Consumer<SendMessage> sender;
    private final String text = "Привет, этот бот помогает искать работу, " +
            "нажми \"зарегистрироваться\" и пройди небольшую регистрацию" +
            " для использования всех возможностей бота";

    public StartProcessor(Consumer<SendMessage> sender, UserInfoRepository repository) {
        this.sender = sender;
        this.repository = repository;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        UserInfo userInfo = new UserInfo();
        userInfo.setTelegramId(message.getWrappedMessage().getChatId());
        userInfo.setUserName(message.getWrappedMessage()
                .getFrom()
                .getUserName());
        userInfo.setCreatedDateTime(LocalDateTime.now());
        userInfo.setLastVisitDateTime(LocalDateTime.now());
        repository.save(userInfo);

        sender.accept(SendMessage.builder()
                .text(text)
                .replyMarkup(getKeyboard())
                .chatId(message.getChatId().toString())
                .build());
    }

    @Override
    public String command() {
        return "/start";
    }

    private ReplyKeyboardMarkup getKeyboard() {
        KeyboardButton button = new KeyboardButton("регистрация");
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(button);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardButtons));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }
}
