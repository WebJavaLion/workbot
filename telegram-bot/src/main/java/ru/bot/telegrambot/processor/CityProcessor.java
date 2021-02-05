package ru.bot.telegrambot.processor;

import org.springframework.beans.factory.annotation.Autowired;
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
import ru.bot.telegrambot.util.KeyboardUtil;
import ru.bot.telegrambot.util.MessageUtil;

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

    private final StageSupplier stageSupplier;

    public CityProcessor(UserInfoRepository repository, Consumer<SendMessage> sender, StageSupplier stageSupplier) {
        this.repository = repository;
        this.sender = sender;
        this.stageSupplier = stageSupplier;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        UserInfo userInfo = message.getExtendedUserInfo().getUserInfo();
        Session session = message.getExtendedUserInfo().getSession();
        userInfo.setCity(message.getText());

        RegistrationStage nextStage =
                stageSupplier.getNextStageForClassConsideringMissedStages(
                        CityProcessor.class,
                        message.getExtendedUserInfo()
                );

        session.setRegistrationStage(nextStage);

        SendMessage sm = new SendMessage();
        sm.setChatId(message.getChatId().toString());
        sm.setText(MessageUtil.getMessageForStage(nextStage));
        if (nextStage == null && (session.getMissed() == null || session.getMissed().length == 0)) {
            modifyMessageAndSessionForFullyRegistered(session, sm);
        } else {
            sm.setText(
                    "Чтобы продолжить регистрацию, " +
                    "нажмите соответствующую кнопку"
            );
            sm.setReplyMarkup(KeyboardUtil.getDefaultKeyboardWithContinueButton());
        }
        session.setState(UserState.default_);
        repository.update(userInfo);
        repository.update(session);
        sender.accept(sm);
    }

    @Override
    public String command() {
        return "/city";
    }
}
