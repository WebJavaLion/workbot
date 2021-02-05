package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;
import ru.bot.telegrambot.util.KeyboardUtil;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
@Transactional
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

        String text = message.getText();
        userInfo.setComeFrom(
                text.indexOf(' ') != -1 ?
                        text.substring(text.indexOf(' ')) : null);

        repository.save(userInfo)
                .ifPresent(id -> {
                    Session session = new Session();
                    session.setId(id);
                    session.setIsFullyRegistered(false);
                    repository.save(session);
                });

        sender.accept(SendMessage.builder()
                .text(this.text)
                .replyMarkup(getKeyboard())
                .chatId(message.getChatId().toString())
                .build()); 
    }

    @Override
    public String command() {
        return "/start";
    }

    private ReplyKeyboardMarkup getKeyboard() {
        return KeyboardUtil.getDefaultKeyboardWithRegistrationButton();
    }
}
