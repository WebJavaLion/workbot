package ru.bot.telegrambot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bot.telegrambot.client.FeignClient;

/**
 * @author Lshilov
 */

@RestController
@RequestMapping("/")
public class TestController {

    private final FeignClient client;

    public TestController(FeignClient client) {
        this.client = client;
    }

    @GetMapping
    String test() {
        return client.test();
    }
}
