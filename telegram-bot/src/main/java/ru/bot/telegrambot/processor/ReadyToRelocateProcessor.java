package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.UserInfo;

import java.util.function.Consumer;

@Component
@Transactional
@RegistrationFlow(order = 5, stage = RegistrationStage.relocate_choice)
public class ReadyToRelocateProcessor extends AbstractRegistrationProcessor{

    private final Consumer<AnswerCallbackQuery> answerSender;

    public ReadyToRelocateProcessor(UserInfoRepository repository,
                                    Consumer<SendMessage> sender,
                                    StageSupplier stageSupplier, Consumer<AnswerCallbackQuery> answerSender) {
        super(repository,sender,stageSupplier);
        this.answerSender = answerSender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        try {
            Boolean.parseBoolean(message.getText());
            super.process(message);
        } catch (Exception e) {
            sender.accept(new SendMessage(message.getChatId().toString(),
                    "Нажмите на кнопку")
            );
        }
    }

    @Override
    protected void processMessage(ExtendedMessageInfo message) {
        String text = message.getText();
        UserInfo userInfo = message.getExtendedUserInfo().getUserInfo();
        userInfo.setIsReadyToRelacate(Boolean.parseBoolean(text));

        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(message.getCallbackQuery().getId())
                .text(text)
                .build();

        answerSender.accept(answerCallbackQuery);
        repository.update(userInfo);
    }

    @Override
    public String command() {
        return "/relocate";
    }
}
