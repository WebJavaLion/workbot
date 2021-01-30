package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.enums.RegistrationStage;
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
public class StartRegistrationProcessor implements Processor {

    private final UserInfoRepository repository;
    private final Consumer<SendMessage> sender;

    public StartRegistrationProcessor(UserInfoRepository repository, Consumer<SendMessage> sender) {
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        Session session = message.getExtendedUserInfo().getSession();
        session.setState(UserState.registration);
        session.setRegistrationStage(RegistrationStage.key_words_choice);
        repository.update(session);
        SendMessage sm = new SendMessage();
        sm.setChatId(message.getChatId().toString());
        sm.setText(
                "Чтобы приостановить регистрацию нажмите \"/stop\", " +
                "чтобы пропустить шаг регистрации нажмите \"/skip\""
        );
        sm.setReplyMarkup(KeyboardUtil.getDefaultKeyboardWithCancelButton());
        sender.accept(sm);
        sender.accept(new SendMessage(message.getChatId().toString(),
                "Введите ключевые слова по которым для Вас будут подбираться вакансии. " +
                        "Например: java, spring, kotlin, php, javascript")
        );
    }

    @Override
    public String command() {
        return "Регистрация";
    }
}
