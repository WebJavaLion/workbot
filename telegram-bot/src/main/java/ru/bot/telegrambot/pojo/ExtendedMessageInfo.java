package ru.bot.telegrambot.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Lshilov
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExtendedMessageInfo {

    private ExtendedUserInfo extendedUserInfo;
    private Message wrappedMessage;
    private MessageType messageType;
    private CallbackQuery callbackQuery;
}
