package ru.bot.telegrambot.pojo;

import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
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

    private String text;
    private Long chatId;
    private Message wrappedMessage;
    private MessageType messageType;
    private CallbackQuery callbackQuery;
    private ExtendedUserInfo extendedUserInfo;
}
