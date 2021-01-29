package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import ru.bot.telegrambot.context.RegistrationFlowSupplier;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.tables.pojos.Session;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Lshilov
 */

@Component
public class DefaultStageResolver implements StageSupplier {

    @RegistrationFlowSupplier
    public Map<Class<?>, RegistrationStage> processorMap;

    @Override
    public RegistrationStage getNextStageForClass(Class<?> cl) {
        return processorMap.get(cl);
    }

    @Override
    public RegistrationStage getNextStageForClassConsideringMissedStages(Class<?> cl, ExtendedUserInfo userInfo) {
        Session session = userInfo.getSession();
        RegistrationStage[] missed = session.getMissed();
        RegistrationStage[] newArray = null;
        if (missed != null && missed.length > 0 &&
                (userInfo.getUserInfo().getCity() != null ||
                        Arrays.asList(missed).contains(RegistrationStage.city_choice))) {
            if (missed.length > 1) {
                newArray = Arrays.copyOfRange(missed, 1, missed.length);
            }
            session.setMissed(newArray);
            return missed[0];
        }
        return getNextStageForClass(cl);
        //TODO: Взять юзер инфо и посмотреть, какие поля уже заполнены,
        // если для следющей стадии уже есть инфа, значит отдать следующую, где ее нет, если все заполнено, то вернуть нул
    }
}
