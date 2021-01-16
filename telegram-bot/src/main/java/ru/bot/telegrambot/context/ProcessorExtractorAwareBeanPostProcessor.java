package ru.bot.telegrambot.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import ru.bot.telegrambot.processor.ProcessorExtractor;

/**
 * @author Lshilov
 */
@Component
public class ProcessorExtractorAwareBeanPostProcessor implements BeanPostProcessor {

    private final ConfigurableApplicationContext applicationContext;

    public ProcessorExtractorAwareBeanPostProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (ProcessorExtractorAware.class.isAssignableFrom(bean.getClass())) {
            ((ProcessorExtractorAware) bean)
                    .setExtractor(applicationContext
                            .getBean(ProcessorExtractor.class));
        }
        return bean;
    }
}
