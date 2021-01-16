package ru.bot.telegrambot.pojo.converter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.MessageType;
import ru.bot.telegrambot.repository.UserInfoRepository;

/**
 * @author Lshilov
 */

@Component
public class ExtendedMessageInfoConverter implements Converter<ExtendedMessageInfo, Update> {

    final UserInfoRepository userRepository;

    public ExtendedMessageInfoConverter(UserInfoRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ExtendedMessageInfo convert(Update ob) {
        ExtendedMessageInfo messageInfo = new ExtendedMessageInfo();
        messageInfo.setCallbackQuery(ob.getCallbackQuery());
        messageInfo.setWrappedMessage(ob.getMessage());

        if (messageInfo.getWrappedMessage() != null) {
            messageInfo.setMessageType(MessageType.DEFAULT);
        } else if (messageInfo.getCallbackQuery() != null) {
            messageInfo.setMessageType(MessageType.CALLBACK);
        }
        messageInfo.setChatId(getChatId(ob));

        messageInfo.setText(
                messageInfo.getWrappedMessage() != null ?
                        messageInfo.getWrappedMessage().getText() : messageInfo.getCallbackQuery().getData());

        userRepository
                .findByTelegramId(
                        messageInfo.getWrappedMessage() != null ?
                                messageInfo.getWrappedMessage().getChatId() :
                                messageInfo.getCallbackQuery()
                                        .getFrom()
                                        .getId()
                                        .longValue()
                )
                .ifPresent(messageInfo::setExtendedUserInfo);

        return messageInfo;
    }

    private Long getChatId(Update update) {
       return update.getMessage() != null ?
        update.getMessage().getChatId() :
        update.getCallbackQuery()
                .getFrom()
                .getId()
                .longValue();
    }
}
