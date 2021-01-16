package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
@Transactional
@RegistrationFlow(order = 4, stage = RegistrationStage.city_choice)
public class CityProcessor implements Processor {

    private final UserInfoRepository repository;
    private final Consumer<SendMessage> sender;

    public CityProcessor(UserInfoRepository repository, Consumer<SendMessage> sender) {
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        UserInfo userInfo = message.getExtendedUserInfo().getUserInfo();
        Session session = message.getExtendedUserInfo().getSession();
        userInfo.setCity(message.getText());
        session.setState(UserState.default_);
        session.setIsFullyRegistered(true);
        session.setRegistrationStage(null);

        repository.update(userInfo);
        repository.update(session);

        sender.accept(SendMessage.builder()
                .text("Спасибо за регистрацию!")
                .chatId(message.getChatId().toString())
                .replyMarkup(getKeyboard())
                .build());
    }

    private ReplyKeyboard getKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        KeyboardButton button = new KeyboardButton("вакансии");
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }

    @Override
    public String command() {
        return "/city";
    }
}
