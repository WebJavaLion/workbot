package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bot.telegrambot.context.RegistrationFlow;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.repository.UserInfoRepository;
import ru.bot.telegrambot.tables.pojos.KeyWord;
import ru.bot.telegrambot.tables.pojos.Session;
import ru.bot.telegrambot.util.KeyboardUtil;
import ru.bot.telegrambot.util.MessageUtil;

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
public class KeyWordsProcessor extends AbstractRegistrationProcessor {

    public KeyWordsProcessor(StageSupplier stageSupplier,
                             UserInfoRepository repository,
                             Consumer<SendMessage> sender) {
        super(repository, sender, stageSupplier);
    }


    @Override
    protected void processMessage(ExtendedMessageInfo message) {
        List<String> words = parseWords(message.getText());

        repository.save(words.stream()
                .map(word -> new KeyWord(message
                        .getExtendedUserInfo()
                        .getUserInfo()
                        .getId(), word)
                )
                .collect(Collectors.toList()));
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
}
