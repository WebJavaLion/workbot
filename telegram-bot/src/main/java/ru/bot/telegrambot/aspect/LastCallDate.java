package ru.bot.telegrambot.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.time.LocalDateTime;


@Component
@Aspect
public class LastCallDate {

    final UserInfoRepository repository;

    public LastCallDate(UserInfoRepository repository) {
        this.repository = repository;
    }

    @After("execution(* ru.bot.telegrambot.processor.Processor.process(..)) && args(extendedMessageInfo)")
    public void lastCall(ExtendedMessageInfo extendedMessageInfo) {
        UserInfo userInfo = extendedMessageInfo.getExtendedUserInfo().getUserInfo();
        userInfo.setLastVisitDateTime(LocalDateTime.now());
        repository.update(userInfo);
    }
}
