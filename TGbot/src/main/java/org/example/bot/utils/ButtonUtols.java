package org.example.bot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ButtonUtols {

    public static InlineKeyboardMarkup getMainMenuButtons() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Виселица");
        button1.setCallbackData("hangmangame");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Вордли");
        button2.setCallbackData("wordlygame");

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Карточная игра");
        button3.setCallbackData("cardgame");

        // Организуем кнопки в строки
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        row1.add(button2);
        row1.add(button3);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row1);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        return markup;
    }

    public static InlineKeyboardMarkup getcardButtons() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Виселица");
        button1.setCallbackData("hangmangame");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Вордли");
        button2.setCallbackData("wordlygame");

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Карточная игра");
        button3.setCallbackData("cardgame");

        // Организуем кнопки в строки
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        row1.add(button2);
        row1.add(button3);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row1);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        return markup;
    }
}

