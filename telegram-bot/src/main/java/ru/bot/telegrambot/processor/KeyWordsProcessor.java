package ru.bot.telegrambot.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.KeyWord;
import ru.bot.telegrambot.tables.pojos.Session;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Lshilov
 */

@Component
@Transactional
@RegistrationFlow(order = 1, stage = RegistrationStage.key_words_choice)
public class KeyWordsProcessor implements Processor {

    private final StageSupplier stageSupplier;
    private final UserInfoRepository repository;
    private final Consumer<SendMessage> sender;

    public KeyWordsProcessor(StageSupplier stageSupplier, UserInfoRepository repository, Consumer<SendMessage> sender) {
        this.stageSupplier = stageSupplier;
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    public void process(ExtendedMessageInfo message) {
        List<String> words = parseWords(message.getText());
        Session session = message.getExtendedUserInfo().getSession();
        RegistrationStage nextStageForClass = stageSupplier.getNextStageForClass(KeyWordsProcessor.class);
        session.setRegistrationStage(nextStageForClass);
        repository.save(words.stream()
                .map(word -> new KeyWord(message
                        .getExtendedUserInfo()
                        .getUserInfo()
                        .getId(), word)
                )
                .collect(Collectors.toList()));
        repository.update(session);
        sender.accept(
                new SendMessage(message.getChatId().toString(),
                        getMessage(nextStageForClass))
        );
    }

    @Override
    public String command() {
        return "/key word";
    }

    private List<String> parseWords(String text) {
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    private String getMessage(RegistrationStage stage) {
        return switch (stage) {
            case city_choice -> "Напишите город, в котором вы проживаете";

            case experience_choice -> """
                    Напишите ваш текущий опыт в данной сфере в следующем формате:
                    менее 6 месяцев, 1-3 года, более 3 лет                                            
                    """;

            case min_salary_choice -> """
                    Введите минимальный желаемый уровень зарплаты в рублях. 
                    Например: 100 тысяч, 100000, 100к
                    """;

            default -> throw new IllegalStateException("Unexpected value: " + stage);
        };
    }
}
