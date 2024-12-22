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

    Card cardGame = new Card(this);
    Handman handmanGame = new Handman(this);
    Wordly wordlyGame = new Wordly(this);

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
                activeGames.put(chatId, cardGame);
                activeGames.get(chatId).gameStarted(chatId);
            }else if (callbackData.equals("main_menu")) {
                sendMessage(chatId, "Вы вернулись в главное меню. Нажмите /start для выбора игры.");
                cardGame.cardWorking=false;
                activeGames.clear();
            }
            if (callbackData.equals("hangmangame")) {
                sendMessage(chatId, "Игра 'Виселица' началась!");
                activeGames.put(chatId, handmanGame);
                activeGames.get(chatId).gameStarted(chatId);
            }

            if (callbackData.equals("wordlygame")) {
                sendMessage(chatId, "Игра 'Вордли' началась!");
                activeGames.put(chatId, wordlyGame);
                activeGames.get(chatId).gameStarted(chatId);
            }
            else if (callbackData.startsWith("card_")) {
                if(cardGame.cardWorking){
                    int cardIndex = Integer.parseInt(callbackData.split("_")[1]);
                    cardGame.handleCardSelection(chatId, cardIndex);
                }else{
                    sendMessage(chatId,"Карточная игра уже завершена, нажмите /start для возврата в гланое меню.");
                }
            }
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
