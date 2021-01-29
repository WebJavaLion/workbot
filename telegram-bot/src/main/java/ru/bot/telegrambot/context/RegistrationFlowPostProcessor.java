package ru.bot.telegrambot.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import ru.bot.telegrambot.enums.RegistrationStage;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Lshilov
 */

@Component
public class RegistrationFlowPostProcessor implements BeanPostProcessor {

    private final Map<Object, List<Field>> supplierFieldsFlow = new HashMap<>();
    private final Map<Class<?>, RegistrationStage> stageFlowMap = new HashMap<>();
    private final Map<Class<?>, Map<RegistrationStage, Object>> stageMapBeans = new HashMap<>();
    private final Map<Integer, Map.Entry<Class<?>, RegistrationStage>> tmpMap = new HashMap<>();
    private final Map<Object, List<Map.Entry<Class<?>, Field>>> supplierStageMap = new HashMap<>();

    @Autowired
    ConfigurableApplicationContext context;

    @PostConstruct
    void init() {
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(RegistrationFlow.class);
    }

    // Вроде работает, но надо тестить в разных ситуациях, можно было бы просто захардкодить, но так красивее. Потом дорефакторю именования и кодстайл.
    //TODO: переписать, заинжектив контекст, и взять нужные бины в постконстракте, а не обновлять кучу мап и постоянно ресетить их в бины
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Arrays.stream(bean.getClass().getDeclaredFields())
                .forEach(field -> {
                    RegistrationFlowSupplier stageFlowSupplierAnnotation =
                            field.getAnnotation(RegistrationFlowSupplier.class);
                    if (stageFlowSupplierAnnotation != null && isValidFieldForStageMap(field)) {
                        if (!supplierFieldsFlow.containsKey(bean)) {
                            supplierFieldsFlow.put(bean, new ArrayList<>());
                        }
                        supplierFieldsFlow.get(bean).add(field);
                        setValue(field, bean, stageFlowMap);
                    }

                    RegistrationStageMap stageMapAnnotation = field.getAnnotation(RegistrationStageMap.class);
                    if (stageMapAnnotation != null && isValidFieldForBeanMap(field)) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        Class<?> clazz = (Class<?>) genericType.getActualTypeArguments()[1];

                        Set<Class<?>> collect = stageMapBeans.keySet()
                                .stream()
                                .filter(clazz::isAssignableFrom)
                                .collect(Collectors.toSet());

                        if (collect.isEmpty()) {
                            supplierStageMap.put(
                                    bean, Lists.newArrayList
                                            (new AbstractMap.SimpleEntry<>(clazz, field)));
                            stageMapBeans.put(clazz, new HashMap<>());
                        } else {
                            Map<RegistrationStage, Object> newMap = new HashMap<>();
                            for (Class<?> aClass : collect) {
                                Map<RegistrationStage, Object> registrationStageObjectMap = stageMapBeans.get(aClass);
                                newMap.putAll(registrationStageObjectMap);
                            }
                            setValue(field, bean, newMap);
                            supplierStageMap.put(
                                    bean, Lists.newArrayList(
                                            new AbstractMap.SimpleEntry<>(clazz, field)));
                        }
                    }
                });

        RegistrationFlow annotation = AnnotationUtils.findAnnotation(bean.getClass(), RegistrationFlow.class);
        if (annotation != null) {
            int order = annotation.order();
            RegistrationStage stage = annotation.stage();

            Set<Class<?>> collect = stageMapBeans.keySet()
                    .stream()
                    .filter(cl -> cl.isAssignableFrom(bean.getClass()))
                    .collect(Collectors.toSet());
            if (!collect.isEmpty()) {
                Map<RegistrationStage, Object> newMap = new HashMap<>();
                collect.stream().map(stageMapBeans::get).forEach(m -> {
                    m.put(stage, bean);
                    newMap.putAll(m);
                });
                supplierStageMap
                        .forEach((key, value) -> value.stream()
                                .filter(e -> e.getKey()
                                        .isAssignableFrom(bean.getClass()))
                                .findAny()
                                .ifPresent(e -> setValue(e.getValue(), key, newMap)));
            } else {
                stageMapBeans.put(bean.getClass(), new HashMap<>(Map.of(stage, bean)));
            }

            stageFlowMap.putIfAbsent(bean.getClass(), stage);
            tmpMap.put(order, new AbstractMap.SimpleEntry<>(bean.getClass(), stage));
            if (tmpMap.get(order + 1) != null) {
                stageFlowMap.put(bean.getClass(), tmpMap.get(order + 1).getValue());
                supplierFieldsFlow.forEach((k, v) ->
                        v.forEach(f -> setValue(f, k, ImmutableMap.copyOf(stageFlowMap))));
            }
            if (tmpMap.get(order - 1) != null) {
                int counter = order;
                while (tmpMap.get(--counter) != null) {
                    Map.Entry<Class<?>, RegistrationStage> currentStage = tmpMap.get(counter);
                    Map.Entry<Class<?>, RegistrationStage> nextStage = tmpMap.get(counter + 1);
                    stageFlowMap.put(currentStage.getKey(), nextStage.getValue());
                }
                supplierFieldsFlow.forEach((k, v)
                        -> v.forEach(f -> setValue(f, k, ImmutableMap.copyOf(stageFlowMap))));
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (AopUtils.isAopProxy(bean)) {
            Object target = AopProxyUtils.getSingletonTarget(bean);
            if (target != null) {
                Set<Class<?>> collect = stageMapBeans.keySet()
                        .stream()
                        .filter(cl -> cl.isAssignableFrom(bean.getClass()))
                        .collect(Collectors.toSet());
                for (Class<?> aClass : collect) {
                    Map<RegistrationStage, Object> registrationStageObjectMap = stageMapBeans.get(aClass);
                    registrationStageObjectMap.forEach((k, v) -> {
                        if (target.equals(v)) {
                            registrationStageObjectMap.put(k, bean);
                            Map<RegistrationStage, Object> newMap = Map.copyOf(registrationStageObjectMap);
                            supplierStageMap
                                    .forEach((key, value) -> value.stream()
                                            .filter(e -> e.getKey()
                                                    .isAssignableFrom(target.getClass()))
                                            .findAny()
                                            .ifPresent(e -> setValue(e.getValue(), key, newMap)));
                        }
                    });
                }
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
}
