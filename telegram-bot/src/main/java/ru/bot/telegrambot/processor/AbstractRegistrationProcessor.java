package ru.bot.telegrambot.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.util.KeyboardUtil;
import ru.bot.telegrambot.util.MessageUtil;

import java.util.function.Consumer;

/**
 * @author Lshilov
 */

public abstract class AbstractRegistrationProcessor implements Processor {

    protected final UserInfoRepository repository;
    protected final Consumer<SendMessage> sender;
    protected final StageSupplier stageSupplier;

    public AbstractRegistrationProcessor(UserInfoRepository repository,
                                         Consumer<SendMessage> sender,
                                         StageSupplier stageSupplier) {
        this.repository = repository;
        this.sender = sender;
        this.stageSupplier = stageSupplier;
    }


    @Override
    public void process(ExtendedMessageInfo message) {
        processMessage(message);
        resolveSession(message);
        sendMessage(message);
    }

    protected abstract void processMessage(ExtendedMessageInfo message);

    private void resolveSession(ExtendedMessageInfo message) {
        RegistrationStage nextStageForClass = stageSupplier
                .getNextStageForClassConsideringMissedStages(
                        this.getClass(),
                        message.getExtendedUserInfo()
                );
        Session session = message.getExtendedUserInfo()
                .getSession();

        session.setRegistrationStage(nextStageForClass);

        if (nextStageForClass == null && (session.getMissed() == null || session.getMissed().length == 0)) {
            session.setIsFullyRegistered(true);
            session.setState(UserState.default_);
        } else if (nextStageForClass == null) {
            session.setState(UserState.default_);
        }

        repository.update(session);
    }

    private void sendMessage(ExtendedMessageInfo message) {
        Session session = message
                .getExtendedUserInfo()
                .getSession();

        SendMessage sm = new SendMessage(message.getChatId().toString(),
                MessageUtil.getMessageForStage(session
                        .getRegistrationStage())
        );
        if (session.getRegistrationStage() == null && (session.getMissed() == null || session.getMissed().length == 0)) {
            sm.setReplyMarkup(KeyboardUtil.getDefaultKeyboard());
        } else if (session.getRegistrationStage() == null) {
            sm.setText("Чтобы пародолжить регистрацию, нажмите соответствующую кнопку!");
            sm.setReplyMarkup(KeyboardUtil.getDefaultKeyboardWithContinueButton());
        }

        sender.accept(sm);
    }
}
