package ru.bot.telegrambot.context;

import com.google.common.collect.ImmutableMap;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import ru.bot.telegrambot.enums.RegistrationStage;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Lshilov
 */

@Component
public class RegistrationFlowPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private int flowCounts;
    private int flowCounter;
    private int stageMapCounts;
    private int stageCounter;
    private boolean isInvoked;

    private ConfigurableApplicationContext context;

    private Map<Class<?>, RegistrationStage> stageFlowMap;
    private final Map<String, Object> registrationStageMapClients = new HashMap<>();
    private final Map<Class<?>, List<Map.Entry<Object, List<Field>>>> target = new HashMap<>();
    private final Map<String, Map.Entry<RegistrationStage, Object>> registrationStageMapBeans = new HashMap<>();

    @PostConstruct
    private void init() {
        String[] registrationFlowAnnotations = context.getBeanNamesForAnnotation(RegistrationFlow.class);
        String[] definitionNames = context.getBeanDefinitionNames();
        for (String name : definitionNames) {
            BeanDefinition beanDefinition = context.getBeanFactory().getBeanDefinition(name);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                try {
                    Class<?> aClass = Class.forName(beanClassName);
                    for (Field declaredField : aClass.getDeclaredFields()) {
                        if (declaredField.getAnnotation(RegistrationStageMap.class) != null) {
                            stageMapCounts++;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Map<Integer, AbstractMap.SimpleEntry<Class<?>, RegistrationStage>> map = new HashMap<>();
        Arrays.stream(registrationFlowAnnotations)
                .forEach(name -> {
                    Class<?> type = context.getType(name);
                    if (type != null) {
                        RegistrationFlow annotation = AnnotationUtils
                                .findAnnotation(type, RegistrationFlow.class);
                        if (annotation != null) {
                            map.put(annotation.order(),
                                    new AbstractMap.SimpleEntry<>(type, annotation.stage())
                            );
                        }
                    }
                });
        Map<Class<?>, RegistrationStage> stageMap = new HashMap<>();
        map.keySet()
                .forEach(k -> {
                    AbstractMap.SimpleEntry<Class<?>, RegistrationStage> current = map.get(k);
                    AbstractMap.SimpleEntry<Class<?>, RegistrationStage> next = map.get(k + 1);
                    stageMap.put(
                            current.getKey(),
                            Objects.requireNonNullElse(next, current).getValue()
                    );
                });
        stageFlowMap = ImmutableMap.copyOf(stageMap);
        flowCounts = registrationFlowAnnotations.length;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Arrays.stream(bean.getClass().getDeclaredFields())
                .forEach(field -> {
                    RegistrationFlowSupplier stageFlowSupplierAnnotation =
                            field.getAnnotation(RegistrationFlowSupplier.class);
                    if (stageFlowSupplierAnnotation != null && isValidFieldForStageMap(field)) {
                        setValue(field, bean, stageFlowMap);
                    }

                    RegistrationStageMap stageMapAnnotation = field.getAnnotation(RegistrationStageMap.class);
                    if (stageMapAnnotation != null && isValidFieldForBeanMap(field)) {
                        registrationStageMapClients.put(beanName, bean);
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        Class<?> clazz = (Class<?>) genericType.getActualTypeArguments()[1];
                        List<Map.Entry<Object, List<Field>>> maps = target.get(clazz);
                        if (maps != null) {
                            maps.stream()
                                    .filter(e -> bean.equals(e.getKey()))
                                    .findAny()
                                    .ifPresent(e -> e.getValue().add(field));
                        } else {
                            List<Map.Entry<Object, List<Field>>> newEntryList = new ArrayList<>();
                            newEntryList.add(new AbstractMap.SimpleEntry<>(bean, List.of(field)));
                            target.put(clazz, newEntryList);
                        }
                        stageCounter++;
                    }
                });

        RegistrationFlow annotation = AnnotationUtils.findAnnotation(bean.getClass(), RegistrationFlow.class);
        if (annotation != null) {
            RegistrationStage stage = annotation.stage();
            registrationStageMapBeans.put(
                    beanName,
                    new AbstractMap.SimpleEntry<>(stage, bean)
            );
        }
        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (registrationStageMapBeans.containsKey(beanName) || registrationStageMapClients.containsKey(beanName)) {
            if (AopUtils.isAopProxy(bean)) {
                registrationStageMapBeans.get(beanName).setValue(bean);
            }
            if (registrationStageMapBeans.containsKey(beanName)) {
                flowCounter++;
            }
            if (flowCounter == flowCounts && !isInvoked && stageCounter == stageMapCounts) {
                target.forEach((k, v) -> {
                    Map.Entry<RegistrationStage, Object>[] entries =
                            registrationStageMapBeans.values()
                                    .stream()
                                    .filter(e -> k.isAssignableFrom(e.getValue().getClass()))
                                    .toArray(Map.Entry[]::new);

                    Map<RegistrationStage, Object> registrationStageObjectMap =
                            ImmutableMap.copyOf(
                                    Map.ofEntries(entries)
                            );
                    v.forEach(entry ->
                            entry.getValue()
                                    .forEach(field ->
                                            setValue(field,
                                                    entry.getKey(),
                                                    registrationStageObjectMap)
                                    )
                    );
                });
                isInvoked = true;
            }
        }
        return bean;
    }

    private void setValue(Field field, Object obj, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("set field was failed");
        }
    }

    private boolean isValidFieldForStageMap(Field field) {
        if (Map.class.isAssignableFrom(field.getType())) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = type.getActualTypeArguments();
            return (((ParameterizedType) actualTypeArguments[0]).getRawType() instanceof Class<?>) &&
                    RegistrationStage.class.isAssignableFrom((Class<?>) actualTypeArguments[1]);
        }
        return false;
    }

    private boolean isValidFieldForBeanMap(Field field) {
        if (Map.class.isAssignableFrom(field.getType())) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = type.getActualTypeArguments();
            return RegistrationStage.class.isAssignableFrom((Class<?>) actualTypeArguments[0]);
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = (ConfigurableApplicationContext) applicationContext;
    }
}
