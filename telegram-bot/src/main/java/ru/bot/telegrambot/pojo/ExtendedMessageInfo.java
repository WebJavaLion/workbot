package ru.bot.telegrambot.pojo;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Lshilov
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ExtendedMessageInfo {

    private ExtendedUserInfo extendedUserInfo;
    private Message wrappedMessage;
    private MessageType messageType;
    private CallbackQuery callbackQuery;
}
