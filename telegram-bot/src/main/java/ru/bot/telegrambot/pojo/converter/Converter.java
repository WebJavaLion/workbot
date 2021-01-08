package ru.bot.telegrambot.pojo.converter;

public interface Converter<T,R> {

    T convert(R ob);
}
