package ru.bot.telegrambot.context;

import ru.bot.telegrambot.enums.RegistrationStage;

import java.lang.annotation.*;

/**
 * @author Lshilov
 */

@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrationFlow {

    int order();
    RegistrationStage stage();
}
