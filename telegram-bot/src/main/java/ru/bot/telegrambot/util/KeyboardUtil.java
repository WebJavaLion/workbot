package ru.bot.telegrambot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

/**
 * @author Lshilov
 */

public final class KeyboardUtil {

    private KeyboardUtil() {
    }

    public static ReplyKeyboardMarkup getDefaultKeyboard() {
        KeyboardButton button1 = new KeyboardButton("Вакансии");
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(button1);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardButtons));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup getDefaultKeyboardWithRegistrationButton() {
        ReplyKeyboardMarkup defaultKeyboard = getDefaultKeyboard();
        List<KeyboardRow> keyboard = defaultKeyboard.getKeyboard();
        keyboard.get(0).add(new KeyboardButton("Регистрация"));
        return defaultKeyboard;
    }

    public static ReplyKeyboardMarkup getDefaultKeyboardWithContinueButton() {
        ReplyKeyboardMarkup defaultKeyboard = getDefaultKeyboard();
        List<KeyboardRow> keyboard = defaultKeyboard.getKeyboard();
        keyboard.get(0).add(new KeyboardButton("Продолжить регистрацию"));
        return defaultKeyboard;
    }

    public static ReplyKeyboardMarkup getDefaultKeyboardWithCancelButton() {
        ReplyKeyboardMarkup defaultKeyboard = getDefaultKeyboard();
        List<KeyboardRow> keyboard = defaultKeyboard.getKeyboard();
        keyboard.get(0).add(new KeyboardButton("/stop"));
        keyboard.get(0).add(new KeyboardButton("/skip"));
        return defaultKeyboard;
    }
}
