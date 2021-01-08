package ru.bot.telegrambot.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.util.Optional;

import static ru.bot.telegrambot.tables.UserInfo.*;

@Repository
public class UserInfoRepository {

    final DSLContext context;

    public UserInfoRepository(DSLContext context) {
        this.context = context;
    }

    public Optional<UserInfo> findByTelegramId(Long id) {
        return context
                .select(USER_INFO.fields())
                .where(USER_INFO.TELEGRAM_ID.eq(id))
                .fetchOptional()
                .map(r -> r.into(UserInfo.class));
    }
}
