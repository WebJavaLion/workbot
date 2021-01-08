package ru.bot.telegrambot.processor;

import ru.bot.telegrambot.pojo.ExtendedMessageInfo;

/**
 * @author Lshilov
 */

public interface Processor {

    void process(ExtendedMessageInfo message);
}
