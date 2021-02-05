package ru.bot.telegrambot.util;

import ru.bot.telegrambot.enums.RegistrationStage;

/**
 * @author Lshilov
 */

public final class MessageUtil {

    private MessageUtil() {}

    public static String getMessageForStage(RegistrationStage stage) {
        if (stage == null) {
            return "Спасибо за регистрацию";
        }
        return switch (stage) {
            case city_choice -> "Напишите город, в котором вы проживаете";

            case experience_choice -> """
                    Напишите ваш текущий опыт в данной сфере:
                    если менее 6 месяцев, отправьте 1, менее 3 лет, отправьте 2, более 3, отправьте 3                                     
                    """;

            case min_salary_choice -> """
                    Введите минимальный желаемый уровень зарплаты в рублях. 
                    Например: 100 тысяч, 100000, 100к
                    """;
            case key_words_choice -> """
                    Введите ключевые слова по которым для Вас будут подбираться вакансии.
                    Например: java, spring, kotlin, php, javascript
                    """;
        };
    }
}
