package ru.bot.telegrambot.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.bot.telegrambot.Keys;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.tables.pojos.KeyWord;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;
import ru.bot.telegrambot.tables.records.UserInfoRecord;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bot.telegrambot.tables.KeyWord.KEY_WORD;
import static ru.bot.telegrambot.tables.Session.SESSION;
import static ru.bot.telegrambot.tables.UserInfo.USER_INFO;

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

    public Optional<Integer> save(UserInfo userInfo) {
        return context.insertInto(USER_INFO)
                .set(context.newRecord(USER_INFO, userInfo))
                .returning()
                .fetchOptional()
                .map(UserInfoRecord::getId);
    }

    public void save(Session session) {
        context.insertInto(SESSION)
                .set(context.newRecord(SESSION, session))
                .execute();
    }

    public void update(Session session) {
        if (session.getId() != null) {
            context.update(SESSION)
                    .set(context.newRecord(SESSION, session))
                    .where(SESSION.ID.eq(session.getId()))
                    .execute();
        }
    }

    public void save(List<KeyWord> keyWords) {
        context.batchInsert(
                keyWords
                        .stream()
                        .map(v -> context.newRecord(KEY_WORD, v))
                        .collect(Collectors.toList()))
                .execute();
    }

    public void update(UserInfo userInfo) {
        if (userInfo.getId() != null) {
            context.update(USER_INFO)
                    .set(context.newRecord(USER_INFO, userInfo))
                    .where(USER_INFO.ID.eq(userInfo.getId()))
                    .execute();
        }
    }
}
