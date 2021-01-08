package ru.bot.telegrambot.pojo.converter;

/**
 * @author Lshilov
 */

public interface Converter<T,R> {

    T convert(R ob);
}
