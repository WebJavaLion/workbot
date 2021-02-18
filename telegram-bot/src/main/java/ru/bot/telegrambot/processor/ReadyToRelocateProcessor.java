package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.util.KeyboardUtil;

import java.util.function.Consumer;

@Component
@Transactional
@RegistrationFlow(order = 5, stage = RegistrationStage.relocate_choice)
public class ReadyToRelocateProcessor extends AbstractRegistrationProcessor{

    public ReadyToRelocateProcessor(UserInfoRepository repository,
                                    Consumer<SendMessage> sender,
                                    StageSupplier stageSupplier) {
        super(repository,sender,stageSupplier);
    }

    @Override
    public void process(ExtendedMessageInfo message){

    }

    @Override
    protected void processMessage(ExtendedMessageInfo message) {

    }

    @Override
    public String command() {
        return "/relocate";
    }
}
