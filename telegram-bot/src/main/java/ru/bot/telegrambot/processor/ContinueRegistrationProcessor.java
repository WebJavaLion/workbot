package ru.bot.telegrambot.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.util.KeyboardUtil;
import ru.bot.telegrambot.util.MessageUtil;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Lshilov
 */
@Component
@Transactional
public class ContinueRegistrationProcessor implements Processor {

    private final Consumer<SendMessage> sender;
    private final UserInfoRepository repository;

    public ContinueRegistrationProcessor(UserInfoRepository repository, Consumer<SendMessage> sender) {
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        Session session = message.getExtendedUserInfo().getSession();
        RegistrationStage[] missed = session.getMissed();
        int length = missed.length;
        if (length > 0) {
            RegistrationStage registrationStage = missed[0];
            session.setRegistrationStage(registrationStage);
            session.setState(UserState.registration);
            RegistrationStage[] newArray = null;
            if (length > 1) {
                newArray = Arrays.copyOfRange(missed, 1, length);
            }
            session.setMissed(newArray);
            repository.update(session);

            SendMessage sm = new SendMessage();
            sm.setChatId(message.getChatId().toString());
            sm.setReplyMarkup(KeyboardUtil.getDefaultKeyboardWithCancelButton());
            sm.setText(
                    "Чтобы приостановить регистрацию нажмите \"/stop\", " +
                    "чтобы пропустить шаг регистрации нажмите \"/skip\""
            );
            sender.accept(sm);
            sender.accept(new SendMessage(
                    message.getChatId().toString(),
                    MessageUtil.getMessageForStage(registrationStage)
            ));
        }
    }

    @Override
    public String command() {
        return "Продолжить регистрацию";
    }
}
