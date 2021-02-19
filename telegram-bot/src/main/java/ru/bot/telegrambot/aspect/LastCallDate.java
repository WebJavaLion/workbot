package ru.bot.telegrambot.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Configuration;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.pojo.MessageType;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.time.LocalDateTime;
import java.util.Optional;


@Aspect
@Component
public class LastCallDate {


    UserInfoRepository repository = new UserInfoRepository(new DefaultDSLContext(SQLDialect.POSTGRES));

    @After("@annotation(ru.bot.telegrambot.aspect.LastCallAspect) && args(update)")
    public void lastCall(JoinPoint jp, Update update){
        Optional<ExtendedUserInfo> extendedUserInfo = repository.findByTelegramId(update.getCallbackQuery().getFrom().getId().longValue());
        UserInfo userInfo = extendedUserInfo.get().getUserInfo();
        userInfo.setLastVisitDateTime(LocalDateTime.now());
        repository.update(userInfo);
    }
}
