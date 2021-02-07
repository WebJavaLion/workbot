package ru.bot.telegrambot.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;
import ru.bot.telegrambot.util.KeyboardUtil;
import ru.bot.telegrambot.util.MessageUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
@Transactional
@RegistrationFlow(order = 2, stage = RegistrationStage.experience_choice)
public class ExperienceProcessor extends AbstractRegistrationProcessor {

    public ExperienceProcessor(UserInfoRepository repository,
                               StageSupplier stageSupplier,
                               Consumer<SendMessage> sender) {
        super(repository, sender, stageSupplier);
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        String s = message.getText().toLowerCase().trim();
        String convert = convert(s);
        if (!"".equals(convert)) {
            super.process(message);
        } else {
            sender.accept(new SendMessage(message.getChatId().toString(),
                    "Введите в правильном формате"));
        }
    }

    @Override
    protected void processMessage(ExtendedMessageInfo message) {
        String s = message.getText().toLowerCase().trim();
        String convert = convert(s);

        ExtendedUserInfo extendedUserInfo = message.getExtendedUserInfo();
        UserInfo userInfo = extendedUserInfo.getUserInfo();
        userInfo.setExperience(convert);

        repository.update(userInfo);
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
