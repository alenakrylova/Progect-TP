package org.example.bot.games;

import org.example.bot.GameHandler;
import org.example.bot.TelegramBotmain;
import org.example.bot.games.helpers.CardConstruct;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Handman implements GameHandler {
    private static final String HANGMAN_WORDS_FILE = "hangman_words.txt";
    private static final Random RANDOM = new Random();
    private final TelegramBotmain telegramBotmain;
    private boolean isFinished;
    private String guessedWord;
    private String chosenWord;
    private int attemptsLeft;

    public Handman(TelegramBotmain telegramBotmain) {
        this.telegramBotmain = telegramBotmain;
    }

    @Override
    public void handleInput(long chatId, String input) {
            if (input.equalsIgnoreCase("/help")) {
                telegramBotmain.sendMessage(chatId, "Цель игры: угадать заданное слово за определённое количество ходов.\n Введите букву, которую хотите проверить.");
            } else if (input.equalsIgnoreCase("/rules")) {
                telegramBotmain.sendMessage(chatId, "Правила игры:\n1. Вам нужно угадать слово, зашифрованное в виде набора звёздочек `*`.\n" +
                        "2. На каждом ходе вводите букву. Если она есть в слове, она откроется.\n" +
                        "3. У вас есть ограниченное число попыток.\n" +
                        "4. Выиграйте, если угадаете всё слово до окончания попыток!");
            }
        if (input.length() != 1) {
            telegramBotmain.sendMessage(chatId, "Введите только одну букву.");
            return;
        }
        char guessedChar = input.toLowerCase().charAt(0);
        boolean found = false;
        StringBuilder updatedWord = new StringBuilder(guessedWord);

        for (int i = 0; i < chosenWord.length(); i++) {
            if (chosenWord.charAt(i) == guessedChar && guessedWord.charAt(i) == '*') {
                updatedWord.setCharAt(i, guessedChar);
                found = true;
            }
        }

        if (found) {
            guessedWord = updatedWord.toString();
            telegramBotmain.sendMessage(chatId, "Угадали! Слово: " + guessedWord);
        } else {
            attemptsLeft--;
            telegramBotmain.sendMessage(chatId, "Неправильно. Осталось попыток: " + attemptsLeft);
        }

        if (guessedWord.equals(chosenWord)) {
                telegramBotmain.sendMessageWithButtons(chatId, "Поздравляю! Вы угадали слово: " + chosenWord, getMainMenuBut());
            isFinished = true;
        } else if (attemptsLeft == 0) {
            telegramBotmain.sendMessageWithButtons(chatId, "Игра окончена! Вы проиграли. Слово было: " + chosenWord, getMainMenuBut());
            isFinished = true;
        }


    }

    @Override
    public void gameStarted(long chatId)  {
        List<String> words = getWordsFromFile();
        if (words != null && !words.isEmpty()) {
            chosenWord = words.get(new Random().nextInt(words.size()));
            //telegramBotmain.sendMessage(chatId, " Слово: " + chosenWord);
            this.guessedWord = "*".repeat(chosenWord.length());
            this.attemptsLeft = 6; // Количество попыток
            telegramBotmain.sendMessage(chatId, " Слово: " + guessedWord);
            telegramBotmain.sendMessage(chatId, "Введите букву для проверки.");
        } else {
            telegramBotmain.sendMessage(chatId,"Слова не найдены.");
        }
    }


    private static List<String> getWordsFromFile() {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(HANGMAN_WORDS_FILE))) {
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
