package ru.bot.telegrambot.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.bot.telegrambot.Keys;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.tables.pojos.KeyWord;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.util.Objects;
import java.util.Optional;

import static ru.bot.telegrambot.tables.UserInfo.*;

/**
 * @author Lshilov
 */

@Repository
public class UserInfoRepository {

    final DSLContext context;

    public UserInfoRepository(DSLContext context) {
        this.context = context;
    }

    public Optional<ExtendedUserInfo> findByTelegramId(Long id) {
        return context
                .selectFrom(USER_INFO)
                .where(USER_INFO.TELEGRAM_ID.eq(id))
                .fetchOptional()
                .map(record ->
                    ExtendedUserInfo.builder()
                            .userInfo(record.into(UserInfo.class))
                            .session(Objects.requireNonNull(record.fetchChild(Keys.SESSION__SESSION_ID_FKEY))
                                    .into(Session.class))
                            .keyWords(record.fetchChildren(Keys.KEY_WORD__KEY_WORD_USER_ID_FKEY)
                                    .into(KeyWord.class))
                            .build()
                );
    }
}
