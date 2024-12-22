package org.example.bot.games;

import org.example.bot.GameHandler;
import org.example.bot.TelegramBotmain;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Wordly implements GameHandler {
    private static final String WORDLY_WORDS_FILE = "wordly_words.txt";
    private final TelegramBotmain telegramBotmain;
    private String guessedWord;
    private String chosenWord;
    private int attemptsLeft = 6;
    private boolean isFinished;

    public Wordly( TelegramBotmain telegramBotmain1) {
        this.telegramBotmain = telegramBotmain1;
    }

    @Override
    public void handleInput(long chatId, String input) {
        String guessedWord = input.toLowerCase();
        if (input.equalsIgnoreCase("/help")) {
            telegramBotmain.sendMessage(chatId, "Цель игры: Угадайте 5-буквенное слово за 6 попыток.\n Введите букву, которую хотите проверить.");
        } else if (input.equalsIgnoreCase("/rules")) {
            telegramBotmain.sendMessage(chatId, "Правила игры:\n1. Вам нужно угадать 5-буквенное слово за 6 попыток.\n" +
                    "После каждой попытки вы получите подсказки:\n" +
                    "Буквы на правильных местах будут в [квадратных скобках]\n" +
                    "Буквы, которые есть в слове, но на неправильных местах, будут в (круглых скобках)" +
                    "Остальные буквы останутся обычными." +
                    "Постарайтесь угадать слово за минимальное количество попыток!");

        }else if (guessedWord.length() != 5) {
            telegramBotmain.sendMessage(chatId,"Введите слово из 5 букв.");
        }
        else if (guessedWord.equals(chosenWord)) {
            telegramBotmain.sendMessageWithButtons(chatId, "Поздравляю! Вы угадали слово: " + chosenWord, getMainMenuBut());
            isFinished = true;
        } else if (attemptsLeft == 0) {
            telegramBotmain.sendMessageWithButtons(chatId, "Игра окончена! Вы проиграли. Слово было: " + chosenWord, getMainMenuBut());
            isFinished = true;
        } else {
                attemptsLeft--;
                telegramBotmain.sendMessage(chatId, "Неправильно. Осталось попыток: " + attemptsLeft);
                String feedback = getFeedback(chosenWord,guessedWord);
                telegramBotmain.sendMessage(chatId, "Подсказка: " + feedback);
        }
    }

    @Override
    public void gameStarted(long chatId) {
        List<String> words = getWordsFromFile();
        if (words != null && !words.isEmpty()) {
            chosenWord = words.get(new Random().nextInt(words.size()));
            telegramBotmain.sendMessage(chatId, "Добро пожаловать в игру Wordle! Угадайте 5-буквенное слово. У вас 6 попыток.");
        } else {
            telegramBotmain.sendMessage(chatId,"Слова не найдены.");
        }

    }
    private static List<String> getWordsFromFile() {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(WORDLY_WORDS_FILE))) {
            // Читаем только первую строку
            String line = reader.readLine();
            if (line != null) {
                // Разделяем строку на слова и добавляем в список
                words.addAll(Arrays.asList(line.split(" ")));
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }

        return words;
    }

    private static String getFeedback(String secretWord, String userGuess) {
        StringBuilder feedback = new StringBuilder();
        boolean[] used = new boolean[5]; // Для отметки использованных букв в загаданном слове

        // Проверяем буквы на правильной позиции (в квадратных скобках)
        for (int i = 0; i < 5; i++) {
            if (userGuess.charAt(i) == secretWord.charAt(i)) {
                feedback.append("[").append(userGuess.charAt(i)).append("]");
                used[i] = true; // Помечаем, что эта буква уже обработана
            } else {
                feedback.append("_"); // Временный символ-заглушка для дальнейшей обработки
            }
        }

        // Проверяем буквы, которые есть в слове, но не на своём месте (в круглых скобках)
        for (int i = 0; i < 5; i++) {
            if (feedback.charAt(i) != '_') continue; // Пропускаем уже угаданные буквы

            boolean found = false;
            for (int j = 0; j < 5; j++) {
                if (!used[j] && userGuess.charAt(i) == secretWord.charAt(j)) {
                    feedback.setCharAt(i, '(');
                    feedback.insert(i + 1, userGuess.charAt(i) + ")");
                    used[j] = true; // Помечаем букву как использованную
                    found = true;
                    break;
                }
            }
            if (!found) {
                feedback.setCharAt(i, userGuess.charAt(i)); // Буква остаётся обычной
            }
        }

        return feedback.toString();
    }

    public static InlineKeyboardMarkup getMainMenuBut() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Домой");
        button1.setCallbackData("main_menu");

        // Организуем кнопки в строки
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row1);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        return markup;
    }

}
