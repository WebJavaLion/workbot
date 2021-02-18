package ru.bot.telegrambot.processor;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
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

import static ru.bot.telegrambot.enums.RegistrationStage.*;

/**
 * @author Lshilov
 */

@Component
public class DefaultStageResolver implements StageSupplier {

    private RegistrationStage finalStage;
    private final Map<Integer, RegistrationStage> stages;
    private Function<ExtendedUserInfo, Boolean> isLastPropertyNull;
    private final Map<RegistrationStage, Function<ExtendedUserInfo, Boolean>> checkInfoPropertyNullMap;

    @RegistrationFlowSupplier
    public Map<Class<?>, RegistrationStage> processorMap;

    public DefaultStageResolver(Map<Integer, RegistrationStage> stages) {
        this.stages = stages;
        checkInfoPropertyNullMap = ImmutableMap
                .of(
                        key_words_choice, info -> CollectionUtils.isEmpty(info.getKeyWords()),
                        experience_choice, info -> info.getUserInfo().getExperience() == null,
                        min_salary_choice, info -> info.getUserInfo().getMinSalary() == null,
                        city_choice, info -> info.getUserInfo().getCity() == null,
                        relocate_choice, info -> info.getUserInfo().getIsReadyToRelocate() == null
                );
    }

    @PostConstruct
    void init() {
        stages.keySet()
                .stream()
                .max(Integer::compareTo)
                .ifPresent(maxVal -> finalStage = stages.get(maxVal));

        isLastPropertyNull = switch (finalStage) {
            case key_words_choice -> checkInfoPropertyNullMap.get(key_words_choice);
            case experience_choice -> checkInfoPropertyNullMap.get(experience_choice);
            case min_salary_choice -> checkInfoPropertyNullMap.get(min_salary_choice);
            case city_choice -> checkInfoPropertyNullMap.get(city_choice);
            case relocate_choice -> checkInfoPropertyNullMap.get(relocate_choice);
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
        if (missed != null && missed.length > 0) {
            return getNextStageByUInfo(userInfo);
        }
        Boolean isAnyPropIsNull = checkInfoPropertyNullMap.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(session.getRegistrationStage()))
                .map(Map.Entry::getValue)
                .reduce((f1, f2) -> info -> f1.apply(info) || f2.apply(info))
                .orElseThrow()
                .apply(userInfo);

        if (!isAnyPropIsNull) {
            return null;
        }
        return getNextStageForClass(cl);
    }

    @Override
    public RegistrationStage getNextStageByUInfo(ExtendedUserInfo userInfo) {
        RegistrationStage registrationStage = userInfo
                .getSession()
                .getRegistrationStage();

        Session session = userInfo.getSession();
        RegistrationStage[] missed = session.getMissed();

        if (!session.getRegistrationStage().equals(finalStage)) {
            Integer orderOfCurrentStage = HashBiMap.create(stages)
                    .inverse()
                    .get(registrationStage);

            RegistrationStage nextStage = null;
            while (stages.containsKey(++orderOfCurrentStage)) {
                RegistrationStage tmp = stages.get(orderOfCurrentStage);
                if (checkInfoPropertyNullMap.get(tmp).apply(userInfo)) {
                    nextStage = tmp;
                    break;
                }
            }
            if (nextStage != null) {
                if (missed[0].equals(nextStage)) {
                    RegistrationStage[] registrationStages = Arrays.copyOfRange(
                            missed, 1, missed.length);

                    session.setMissed(registrationStages);
                }
                return nextStage;
            }
        }
        return null;
    }
}
