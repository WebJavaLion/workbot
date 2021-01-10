package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;

/**
 * @author Lshilov
 */

@Component
@RegistrationFlow(order = 3, stage = RegistrationStage.min_salary_choice)
public class SalaryProcessor implements Processor {

    final StageSupplier stageSupplier;

    public SalaryProcessor(StageSupplier stageSupplier) {
        this.stageSupplier = stageSupplier;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        stageSupplier.getNextStageForClass(SalaryProcessor.class);
    }
}
