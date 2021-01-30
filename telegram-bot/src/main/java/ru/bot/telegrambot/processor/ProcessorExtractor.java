package ru.bot.telegrambot.processor;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bot.telegrambot.context.RegistrationStageMap;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.enums.UserState;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.tables.pojos.Session;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lshilov
 */

@Component
public class ProcessorExtractor {

    @RegistrationStageMap
    public Map<RegistrationStage, Processor> registrationProcessorMap;

    private final Map<String, Processor> processorMap;

    public ProcessorExtractor(@Autowired List<Processor> processors) {
        processorMap = ImmutableMap
                .copyOf(processors.stream()
                        .collect(Collectors.toMap(
                                Processor::command,
                                Function.identity()))
                );
    }

    public Optional<Processor> getAppropriateProcessor(ExtendedMessageInfo messageInfo) {
        ExtendedUserInfo extendedUserInfo = messageInfo.getExtendedUserInfo();
        Optional<Processor> processorOptional;

        if (extendedUserInfo != null) {
            Session session = extendedUserInfo.getSession();
            UserState state = session.getState();

            if (UserState.registration.equals(state) &&
                    !"/stop".equals(messageInfo.getText()) && !"/skip".equals(messageInfo.getText())) {
                RegistrationStage registrationStage = session.getRegistrationStage();
                processorOptional = Optional.ofNullable(registrationProcessorMap.get(registrationStage));
            } else {
                processorOptional = Optional.ofNullable(processorMap.get(messageInfo.getText()));
            }
        } else {
            processorOptional = Optional.ofNullable(processorMap.get("/start"));
        }
        return processorOptional;
    }
}
