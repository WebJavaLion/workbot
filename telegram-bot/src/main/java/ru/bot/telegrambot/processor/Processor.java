package ru.bot.telegrambot.processor;

import ru.bot.telegrambot.pojo.ExtendedMessageInfo;

public interface Processor {

    void process(ExtendedMessageInfo message);
}
