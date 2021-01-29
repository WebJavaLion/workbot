package ru.bot.telegrambot.processor;

import ru.bot.telegrambot.enums.RegistrationStage;

/**
 * @author Lshilov
 */

public interface StageSupplier {

    RegistrationStage getNextStageForClass(Class<?> cl);
}
