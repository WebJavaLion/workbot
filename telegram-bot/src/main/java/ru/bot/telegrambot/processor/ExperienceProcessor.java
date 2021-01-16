package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;

/**
 * @author Lshilov
 */

@Component
@Transactional
@RegistrationFlow(order = 2, stage = RegistrationStage.experience_choice)
public class ExperienceProcessor implements Processor {

    @Override
    public void process(ExtendedMessageInfo message) {
        System.out.println("151345134531245234523542345234523452354234523543254");
    }

    @Override
    public String command() {
        return "/experience";
    }
}
