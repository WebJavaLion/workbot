package ru.bot.telegrambot.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;



@Component
@Aspect
public class LastCallDate {

    final UserInfoRepository repository;

    public LastCallDate(UserInfoRepository repository) {
        this.repository = repository;
    }

    @After("execution(* ru.bot.telegrambot.processor.Processor.process(..)) && args(extendedMessageInfo)")
    public void lastCall(ExtendedMessageInfo extendedMessageInfo) {
        System.out.println(extendedMessageInfo.getChatId());

//        Optional<ExtendedUserInfo> extendedUserInfo = repository.findByTelegramId(update.getCallbackQuery().getFrom().getId().longValue());
//        UserInfo userInfo = extendedUserInfo.get().getUserInfo();
//        userInfo.setLastVisitDateTime(LocalDateTime.now());
//        repository.update(userInfo);
    }
}
