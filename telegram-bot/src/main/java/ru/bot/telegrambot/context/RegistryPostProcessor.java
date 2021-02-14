package ru.bot.telegrambot.context;

import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.signature.qual.Identifier;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import ru.bot.telegrambot.enums.RegistrationStage;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Lshilov
 *
 * @see <a
 *      href=https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/support/BeanDefinitionRegistryPostProcessor.html"
 *      >spring-doc
 *      </a>
 * @implSpec This post processor creates and registers bean definition for bot registration stages
 * @implNote Bean definition with bean supplier of type Map<Integer, RegistrationStage> will be registered in app context
 */

@Component
@Log4j2
public class RegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    
    private final ImmutableMap.Builder<Integer, RegistrationStage> builder = ImmutableMap.builder();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        Arrays.stream(beanDefinitionRegistry.getBeanDefinitionNames())
                .forEach(name -> {
                    BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(name);
                    try {
                        if (beanDefinition.getBeanClassName() != null) {
                            Class<?> aClass = Class.forName(beanDefinition.getBeanClassName());
                            RegistrationFlow annotation = aClass.getAnnotation(RegistrationFlow.class);

                            if (annotation != null) {
                                int order = annotation.order();
                                RegistrationStage stage = annotation.stage();
                                log.info("registration stage: {}, order: {}", order, stage);
                                builder.put(order, stage);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Map.class, builder::build);
        rootBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        rootBeanDefinition.setAutowireCandidate(true);
        rootBeanDefinition.setTargetType(
                ResolvableType
                        .forClassWithGenerics(
                                Map.class,
                                Integer.class, RegistrationStage.class)
        );
        beanDefinitionRegistry.registerBeanDefinition("stages", rootBeanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

}
