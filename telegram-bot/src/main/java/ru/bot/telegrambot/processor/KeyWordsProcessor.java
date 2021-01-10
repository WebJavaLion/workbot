package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;

/**
 * @author Lshilov
 */

@Component
@RegistrationFlow(order = 1, stage = RegistrationStage.key_words_choice)
public class KeyWordsProcessor implements Processor{

    @Override
    public void process(ExtendedMessageInfo message) {

    }
}
