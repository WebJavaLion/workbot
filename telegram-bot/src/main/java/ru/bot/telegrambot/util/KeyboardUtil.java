package ru.bot.telegrambot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.bot.telegrambot.enums.RegistrationStage;

import java.util.ArrayList;
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

    public static InlineKeyboardMarkup getInlineKeyboard(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLIne = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData("true");
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData("true");
        rowInLIne.add(yesButton);
        rowInLIne.add(noButton);
        rowsInLine.add(rowInLIne);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
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
    public static ReplyKeyboardMarkup getInlineKeyboardByStage(RegistrationStage stage){
        return switch (stage){
            case relocate_choice -> getDefaultKeyboard();
            default -> null;
        };
    }
}
