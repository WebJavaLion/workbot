package ru.bot.telegrambot.service;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.bot.telegrambot.aspect.LastCallAspect;
import ru.bot.telegrambot.configuration.BotProperties;
import ru.bot.telegrambot.context.ProcessorExtractorAware;
import ru.bot.telegrambot.pojo.ExtendedMessageInfo;
import ru.bot.telegrambot.pojo.converter.Converter;
import ru.bot.telegrambot.processor.ProcessorExtractor;

/**
 * @author Lshilov
 */
@EnableAspectJAutoProxy
public class BotService extends TelegramLongPollingBot implements ProcessorExtractorAware {

    private final BotProperties botProperties;
    private ProcessorExtractor processorExtractor;
    private final Converter<ExtendedMessageInfo, Update> converter;

    public BotService(BotProperties botProperties,
                      Converter<ExtendedMessageInfo, Update> converter) {
        this.converter = converter;
        this.botProperties = botProperties;
    }


    public void executeSendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getBotToken();
    }

    @Override
    @LastCallAspect
    public void onUpdateReceived(Update update) {
       ExtendedMessageInfo convert = converter.convert(update);
        processorExtractor.getAppropriateProcessor(convert)
             .ifPresent(processor -> processor.process(convert));
    }

    @Override
    public void setExtractor(ProcessorExtractor extractor) {
        this.processorExtractor = extractor;
    }
}
