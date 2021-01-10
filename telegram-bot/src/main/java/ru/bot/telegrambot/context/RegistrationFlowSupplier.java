package ru.bot.telegrambot.context;

import java.lang.annotation.*;

/**
 * @author Lshilov
 */

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrationFlowSupplier {
}
