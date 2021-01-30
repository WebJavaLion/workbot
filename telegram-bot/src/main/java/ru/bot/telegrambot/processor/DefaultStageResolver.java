package ru.bot.telegrambot.processor;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.bot.telegrambot.context.RegistrationFlowSupplier;
import ru.bot.telegrambot.enums.RegistrationStage;
import ru.bot.telegrambot.pojo.ExtendedUserInfo;
import ru.bot.telegrambot.tables.pojos.Session;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Lshilov
 */

@Component
public class DefaultStageResolver implements StageSupplier {

    private RegistrationStage finalStage;
    private final Map<Integer, RegistrationStage> stages;
    private Function<ExtendedUserInfo, Boolean> isLastPropertyNull;

    @RegistrationFlowSupplier
    public Map<Class<?>, RegistrationStage> processorMap;

    public DefaultStageResolver(Map<Integer, RegistrationStage> stages) {
        this.stages = stages;
    }

    @PostConstruct
    void init() {
        stages.keySet()
                .stream()
                .max(Integer::compareTo)
                .ifPresent(maxVal -> finalStage = stages.get(maxVal));

        isLastPropertyNull = switch (finalStage) {
            case key_words_choice -> info -> CollectionUtils.isEmpty(info.getKeyWords());
            case experience_choice -> info -> info.getUserInfo().getExperience() == null;
            case min_salary_choice -> info -> info.getUserInfo().getMinSalary() == null;
            case city_choice -> info -> info.getUserInfo().getCity() == null;
        };
    }

    @Override
    public RegistrationStage getNextStageForClass(Class<?> cl) {
        return processorMap.get(cl);
    }

    @Override
    public RegistrationStage getNextStageForClassConsideringMissedStages(Class<?> cl, ExtendedUserInfo userInfo) {
        Session session = userInfo.getSession();
        RegistrationStage[] missed = session.getMissed();
        RegistrationStage[] newArray = null;
        if (missed != null && missed.length > 0 &&
                (!isLastPropertyNull.apply(userInfo)  ||
                        Arrays.asList(missed).contains(finalStage))) {
            if (missed.length > 1) {
                newArray = Arrays.copyOfRange(missed, 1, missed.length);
            }
            session.setMissed(newArray);
            return missed[0];
        }
        return getNextStageForClass(cl);
    }
}
