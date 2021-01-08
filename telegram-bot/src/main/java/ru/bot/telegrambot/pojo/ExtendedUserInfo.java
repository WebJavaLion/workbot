package ru.bot.telegrambot.pojo;

import lombok.Builder;
import lombok.Data;
import ru.bot.telegrambot.tables.pojos.KeyWord;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.util.List;

/**
 * @author Lshilov
 */

@Data
@Builder
public class ExtendedUserInfo {

    private UserInfo userInfo;
    private Session session;
    private List<KeyWord> keyWords;

}
