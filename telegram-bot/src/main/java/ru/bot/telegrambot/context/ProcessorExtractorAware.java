package ru.bot.telegrambot.context;

import org.springframework.beans.factory.Aware;
import ru.bot.telegrambot.processor.ProcessorExtractor;

/**
 * @author Lshilov
 */

public interface ProcessorExtractorAware extends Aware {

    void setExtractor(ProcessorExtractor extractor);
}
