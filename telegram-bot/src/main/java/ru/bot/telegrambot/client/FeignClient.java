package ru.bot.telegrambot.client;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Lshilov
 */
@org.springframework.cloud.openfeign.FeignClient(name = "stealer")
public interface FeignClient {

    @GetMapping("vacancy/test")
    String test();
}
