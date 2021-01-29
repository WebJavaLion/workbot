package ru.bot.telegrambot.processor;

import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;

/**
 * @author Lshilov
 */

public interface StageSupplier {

    RegistrationStage getNextStageForClass(Class<?> cl);
    RegistrationStage getNextStageForClassConsideringMissedStages(Class<?> cl, ExtendedUserInfo userInfo);
}
