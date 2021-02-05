package ru.bot.telegrambot.processor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.util.KeyboardUtil;

/**
 * @author Lshilov
 */

public interface Processor {

    void process(ExtendedMessageInfo message);
    String command();

    default void modifyMessageAndSessionForFullyRegistered(Session session, SendMessage sendMessage) {
        modifySession(session);
        modifyMessage(sendMessage);
    }
    private void modifySession(Session session) {
        session.setIsFullyRegistered(true);
        session.setState(UserState.default_);
    }
    private void modifyMessage(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(KeyboardUtil.getDefaultKeyboard());
    }
}
