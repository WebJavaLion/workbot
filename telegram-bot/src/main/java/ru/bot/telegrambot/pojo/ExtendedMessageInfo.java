package ru.bot.telegrambot.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExtendedMessageInfo {

    private UserInfo userInfo;
    private Session userSession;
    private Message wrappedMessage;
    private MessageType messageType;
    private CallbackQuery callbackQuery;
}
