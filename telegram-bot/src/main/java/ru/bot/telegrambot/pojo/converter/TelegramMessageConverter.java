package ru.bot.telegrambot.pojo.converter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;

/**
 * @author Lshilov
 */

@Component
public class TelegramMessageConverter implements Converter<SendMessage, ExtendedMessageInfo> {

    @Override
    public SendMessage convert(ExtendedMessageInfo ob) {
        SendMessage sm = new SendMessage();
        sm.setChatId(ob.getExtendedUserInfo()
                .getUserInfo()
                .getTelegramId()
                .toString()
        );
        sm.setText(ob.getWrappedMessage().getText());
        return sm;
    }
}
