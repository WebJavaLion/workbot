package ru.bot.telegrambot.processor;

import com.google.common.collect.HashBiMap;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.util.KeyboardUtil;
import ru.bot.telegrambot.util.MessageUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
public class SkipStageProcessor implements Processor {

    public Map<Integer, RegistrationStage> stages;

    private final UserInfoRepository repository;
    private final Consumer<SendMessage> sender;
    public SkipStageProcessor(UserInfoRepository repository, Consumer<SendMessage> sender) {
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        Session session = message.getExtendedUserInfo().getSession();
        RegistrationStage registrationStage = session.getRegistrationStage();
        RegistrationStage[] missed = session.getMissed();
        RegistrationStage[] newMissedArray = Arrays.copyOf(missed, missed.length + 1);

        newMissedArray[newMissedArray.length - 1] = registrationStage;
        session.setMissed(newMissedArray);
        //убрать в постконстракт, после того, как будет переписан постпроцессор, сейчас после инит метода мапа может быть пустой
        Integer order = HashBiMap.create(stages)
                .inverse()
                .get(registrationStage);
        RegistrationStage nextStage = stages.get(order + 1);
        session.setRegistrationStage(nextStage);

        SendMessage sm = new SendMessage();
        sm.setChatId(message.getChatId().toString());

        if (nextStage != null) {
            sm.setText(MessageUtil.getMessageForStage(nextStage));
        } else {
            session.setState(UserState.default_);
            sm.setText("чтобы заполнить пропущенные пункты нажмите \"продолжить регистрацию\"");
            sm.setReplyMarkup(KeyboardUtil.getDefaultKeyboardWithContinueButton());
        }
        repository.update(session);
        sender.accept(sm);
    }

    @Override
    public String command() {
        return "/skip";
    }
}
