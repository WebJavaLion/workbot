package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
@Transactional
@RegistrationFlow(order = 4, stage = RegistrationStage.city_choice)
public class CityProcessor extends AbstractRegistrationProcessor {


    public CityProcessor(UserInfoRepository repository,
                         Consumer<SendMessage> sender,
                         StageSupplier stageSupplier) {
        super(repository, sender, stageSupplier);

    }

    @Override
    protected void processMessage(ExtendedMessageInfo message) {
        UserInfo userInfo = message.getExtendedUserInfo().getUserInfo();
        userInfo.setCity(message.getText());
        repository.update(userInfo);
    }

    @Override
    public String command() {
        return "/city";
    }
}
