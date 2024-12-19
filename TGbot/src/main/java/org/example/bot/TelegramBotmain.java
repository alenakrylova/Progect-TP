package org.example.bot;

import org.checkerframework.checker.units.qual.C;
import org.example.bot.games.Card;
import org.example.bot.games.Handman;
import org.example.bot.games.Wordly;
import org.example.bot.utils.ButtonUtols;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelegramBotmain extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "GamesForTPbot";
    }

    @Override
    public String getBotToken() {
        return "7611929913:AAGbp0Eu-ZEiRnymqgbo-2uoPYfkQk1eePo";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Главное меню
            if (messageText.equalsIgnoreCase("/start")) {
                sendMessageWithButtons(chatId, "Выберите игру:", ButtonUtols.getMainMenuButtons());
            } else if (activeGames.containsKey(chatId)) {
                // Передаем управление активной игре
                GameHandler game = activeGames.get(chatId);
                game.handleInput(chatId, messageText);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("cardgame")) {
                sendMessage(chatId, "Игра 'Карточная игра' началась!");
                activeGames.put(chatId, new Card(this));
                activeGames.get(chatId).gameStarted(chatId);
            }
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        if (data.equals("main_menu")) {
            // Завершаем текущую игру
            if (activeGames.containsKey(chatId)) {
                activeGames.remove(chatId);
            }
            // Отправляем сообщение о возвращении в главное меню
            sendMessage(chatId, "Вы вернулись в главное меню. Нажмите /start для выбора игры.");
        } else if (activeGames.containsKey(chatId)) {
            // Если игра активна, передаем управление ей
            GameHandler game = activeGames.get(chatId);
            game.handleInput(chatId, data);
        }else {
            sendMessage(chatId, "Неизвестная команда. Нажмите /start для начала.");
        }
    }




    public void sendMessage(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithButtons(long chatId, String text, InlineKeyboardMarkup buttons) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            message.setReplyMarkup(buttons);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private final Map<Long, GameHandler> activeGames = new HashMap<>();
}
