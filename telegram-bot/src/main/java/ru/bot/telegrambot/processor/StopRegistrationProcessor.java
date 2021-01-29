package ru.bot.telegrambot.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.util.KeyboardUtil;

import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
public class StopRegistrationProcessor implements Processor {

    private final UserInfoRepository repository;
    private final Consumer<SendMessage> sender;

    public StopRegistrationProcessor(UserInfoRepository repository, Consumer<SendMessage> sender) {
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        Session session = message.getExtendedUserInfo().getSession();
        if (!session.getIsFullyRegistered() && session.getState().equals(UserState.registration)) {
            session.setState(UserState.default_);
            repository.update(session);
            sendMessage.setText("чтобы продолжить регистрацию нажмите \"Продолжить ргеистрацию\"");
            sendMessage.setReplyMarkup(KeyboardUtil.getDefaultKeyboardWithContinueButton());

            sender.accept(sendMessage);
        }
    }

    @Override
    public String command() {
        return "/stop";
    }
}
