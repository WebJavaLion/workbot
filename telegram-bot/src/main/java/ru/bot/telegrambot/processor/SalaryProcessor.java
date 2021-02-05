package ru.bot.telegrambot.processor;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.tables.pojos.UserInfo;
import ru.bot.telegrambot.util.KeyboardUtil;
import ru.bot.telegrambot.util.MessageUtil;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;

/**
 * @author Lshilov
 */

@Component
@Log4j2
@Transactional
@RegistrationFlow(order = 3, stage = RegistrationStage.min_salary_choice)
public class SalaryProcessor implements Processor {

    private final StageSupplier stageSupplier;
    private final UserInfoRepository repository;
    private final Consumer<SendMessage> sender;

    public SalaryProcessor(StageSupplier stageSupplier, UserInfoRepository repository, Consumer<SendMessage> sender) {
        this.stageSupplier = stageSupplier;
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        RegistrationStage stage = stageSupplier
                .getNextStageForClassConsideringMissedStages(
                        SalaryProcessor.class,
                        message.getExtendedUserInfo()
                );

        Integer salary = parseTextToSalary(message.getText());
        if (salary != null) {
            ExtendedUserInfo extendedUserInfo = message.getExtendedUserInfo();
            UserInfo userInfo = extendedUserInfo.getUserInfo();
            userInfo.setMinSalary(salary);
            Session session = extendedUserInfo.getSession();
            session.setRegistrationStage(stage);

            SendMessage sm = new SendMessage(
                    message.getChatId().toString(),
                    MessageUtil.getMessageForStage(stage)
            );
            if (stage == null && (session.getMissed() == null || session.getMissed().length == 0)) {
                modifyMessageAndSessionForFullyRegistered(session, sm);
            } else if (stage == null) {
                session.setState(UserState.default_);
                sm.setText("чтобы продолжить, нажмите кнопку");
                sm.setReplyMarkup(KeyboardUtil.getDefaultKeyboardWithContinueButton());
            }
            repository.update(userInfo);
            repository.update(session);

            sender.accept(sm);
        } else {
            sender.accept(new SendMessage(message.getChatId().toString(),
                    "Бот не смог определить вашу желаемую зарплату, попробуйте ввести по-другому"));
        }
    }

    private Integer parseTextToSalary(String text) {
        text = text.trim();
        Integer result = null;
        try {
            result =  Integer.parseInt(text);
        } catch (Exception e) {
            log.warn("Default parse was failed, input string is: \"{}\"", text);
            int k = text.lastIndexOf("к");
            k = k != -1 ? k : text.lastIndexOf("k");
            if (k != -1 && k == (text.length() - 1)) {
                try {
                    result = Integer.parseInt(text.substring(0, text.length() - 1).trim()) * 1000;
                } catch (Exception e1) {
                    log.error("parse was failed, input string is: \"{}\"", text);
                }
            } else if (text.endsWith("тысяч")) {
                String test = text.split("тысяч")[0].trim();
                try {
                    result =  Integer.parseInt(test) * 1000;
                } catch (Exception e2) {
                    log.error("parse was failed, input string is: \"{}\"", text);
                }
            }
        }
        return result;
    }

    @Override
    public String command() {
        return "/salary";
    }
}
