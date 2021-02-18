package ru.bot.telegrambot.processor;

import com.google.common.collect.HashBiMap;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final Map<Integer, RegistrationStage> stages;
    private final UserInfoRepository repository;
    private final Consumer<SendMessage> sender;
    private final StageSupplier stageSupplier;

    public SkipStageProcessor(UserInfoRepository repository,
                              Consumer<SendMessage> sender,
                              Map<Integer, RegistrationStage> stages, StageSupplier stageSupplier) {
        this.repository = repository;
        this.sender = sender;
        this.stages = stages;
        this.stageSupplier = stageSupplier;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        Session session = message.getExtendedUserInfo().getSession();
        RegistrationStage registrationStage = session.getRegistrationStage();
        RegistrationStage[] missed = session.getMissed();
        RegistrationStage[] newMissedArray;
        if (missed != null) {
            if (!Arrays.asList(missed).contains(registrationStage)) {
                newMissedArray = Arrays.copyOf(missed, missed.length + 1);
            } else {
                newMissedArray = new RegistrationStage[missed.length];
                System.arraycopy(missed, 1, newMissedArray, 0, missed.length - 1);
            }
            newMissedArray[newMissedArray.length - 1] = registrationStage;
        } else {
            newMissedArray = new RegistrationStage[] { registrationStage };
        }
        session.setMissed(newMissedArray);
        RegistrationStage nextStage = stageSupplier.getNextStageByUInfo(message.getExtendedUserInfo());

        session.setRegistrationStage(nextStage);

        SendMessage sm = new SendMessage();
        sm.setChatId(message.getChatId().toString());
        sm.setReplyMarkup(KeyboardUtil.getInlineKeyboardByStage(nextStage));
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
