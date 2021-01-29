package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import ru.bot.telegrambot.context.RegistrationFlowSupplier;
import ru.bot.telegrambot.enums.RegistrationStage;

import java.util.Map;

/**
 * @author Lshilov
 */

@Component
public class DefaultSupplier implements StageSupplier {

    @RegistrationFlowSupplier
    public Map<Class<?>, RegistrationStage> processorMap;

    @Override
    public RegistrationStage getNextStageForClass(Class<?> cl) {
        return processorMap.get(cl);
    }

}
