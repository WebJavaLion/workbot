package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;

/**
 * @author Lshilov
 */

@Component
@RegistrationFlow(order = 2, stage = RegistrationStage.experience_choice)
public class ExperienceProcessor implements Processor {

    @Override
    public void process(ExtendedMessageInfo message) {

    }
}
