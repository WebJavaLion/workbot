package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;
import ru.bot.telegrambot.util.MessageUtil;

import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
@Transactional
@RegistrationFlow(order = 2, stage = RegistrationStage.experience_choice)
public class ExperienceProcessor implements Processor {

    private final UserInfoRepository repository;
    private final StageSupplier stageSupplier;
    private final Consumer<SendMessage> sender;

    public ExperienceProcessor(UserInfoRepository repository,
                               StageSupplier stageSupplier,
                               Consumer<SendMessage> sender) {
        this.repository = repository;
        this.stageSupplier = stageSupplier;
        this.sender = sender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        String s = message.getText().toLowerCase().trim();
        String convert = convert(s);
        if (!"".equals(convert)) {
            RegistrationStage nextStageForClass = stageSupplier.getNextStageForClass(ExperienceProcessor.class);
            ExtendedUserInfo extendedUserInfo = message.getExtendedUserInfo();
            Session session = extendedUserInfo.getSession();
            session.setRegistrationStage(nextStageForClass);
            UserInfo userInfo = extendedUserInfo.getUserInfo();
            userInfo.setExperience(convert);
            repository.update(userInfo);
            repository.update(session);
            sender.accept(new SendMessage(message.getChatId().toString(),
                    MessageUtil.getMessageForStage(nextStageForClass))
            );
        } else {
            sender.accept(new SendMessage(message.getChatId().toString(),
                    "Введите в правильном формате"));
        }
    }

    String convert(String text) {
        return switch (text) {
            case "1" -> "менее 6 месяцев";
            case "2" -> "менее 3 лет";
            case "3" -> "более 3 лет";
            default -> "";
        };
    }


    @Override
    public String command() {
        return "/experience";
    }
}
