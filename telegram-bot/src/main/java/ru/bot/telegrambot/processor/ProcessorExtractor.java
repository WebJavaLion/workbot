package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.tables.pojos.Session;

/**
 * @author Lshilov
 */

@Component
public class ProcessorExtractor {


    public Processor getAppropriateProcessor(ExtendedMessageInfo messageInfo) {
        ExtendedUserInfo extendedUserInfo = messageInfo.getExtendedUserInfo();

        if (extendedUserInfo != null) {
            Session session = extendedUserInfo.getSession();
            UserState state = session.getState();
            if (UserState.registration.equals(state)) {
                RegistrationStage registrationStage = session.getRegistrationStage();
                registrationStage.getCatalog();
            } else {

            }
        } else {

        }
        return null;
    }
}
