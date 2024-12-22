package org.example.bot.games;

import org.example.bot.GameHandler;
import org.example.bot.TelegramBotmain;
import org.example.bot.games.helpers.CardConstruct;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class Card implements GameHandler {
    private final TelegramBotmain telegramBotmain;
    private int answNum;
    private int cardsLeft;
    private final Random random = new Random();
    private int cardstoAnsw;
    public boolean isFinished;
    private int targetNumber; // Целевое число
    public int currentSum; // Текущая сумма
    public int movesLeft; // Число оставшихся ходов
    private HashMap<Integer, CardConstruct> cardMap;
    public boolean cardWorking;


    public Card(TelegramBotmain telegramBotmain) {
        this.telegramBotmain = telegramBotmain;
        this.isFinished = false;
        this.cardMap = new HashMap<>();
    }

    @Override
    public void handleInput(long chatId, String input) {
        if (input.equalsIgnoreCase("/help")) {
            telegramBotmain.sendMessage(chatId, "Цель игры: с помощью определенного числа ходов достичь целевого числа.\n" +
                    "Красные карты (♥, ♦) прибавляют значение, черные (♠, ♣) — вычитают.\n" +
                    "Выберите карту, нажав на кнопку.");
        } else if (input.equalsIgnoreCase("/rules")) {
            telegramBotmain.sendMessage(chatId, "Правила игры:\n" +
                    "1. Целевое число и количество ходов выдаются в начале игры.\n" +
                    "2. Нажимайте на кнопки с картами, чтобы выбирать карты.\n" +
                    "3. Красные карты (♥, ♦) прибавляют значение, черные (♠, ♣) — вычитают.\n" +
                    "4. Если вы достигли целевого числа или у вас закончились ходы, игра завершится.");
        } else {
            telegramBotmain.sendMessage(chatId, "Вы отправили недопустимый символ. Выберите карту или вернитесь в главное меню.");
        }
    }

    private void initializeCards() {
        cardMap.clear(); // Очищаем карту при новом запуске игры
        CardConstruct cards = new CardConstruct();
        for (int i = 0; i < 15; i++) {
            cards.cartCreator(cardMap, i);
        }
        targetNumber = random.nextInt(16) + 15; // Число от 15 до 30
        currentSum = 0;
        movesLeft = random.nextInt(3) + 3;
        cardsLeft = cardMap.size(); // Устанавливаем количество карт
        isFinished = false;
        cardWorking = true;
    }

    // Метод для создания клавиатуры с картами
    private InlineKeyboardMarkup createCardKeyboard() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < cardMap.size(); i++) {
            CardConstruct card = cardMap.get(i);

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(card.toString()); // Отображаем значение и масть карты
            button.setCallbackData("card_" + i); // Уникальный идентификатор кнопки

            // Добавляем кнопку в строку
            if (rows.isEmpty() || rows.get(rows.size() - 1).size() == 5) {
                rows.add(new ArrayList<>()); // Начинаем новую строку
            }
            rows.get(rows.size() - 1).add(button);
        }

        InlineKeyboardButton mainMenuButton = new InlineKeyboardButton();
        mainMenuButton.setText("Вернуться в главное меню");
        mainMenuButton.setCallbackData("main_menu");
        rows.add(Collections.singletonList(mainMenuButton)); // Один ряд с одной кнопкой

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    // Метод отправки клавиатуры
    private void sendCardKeyboard(long chatId) {
        InlineKeyboardMarkup keyboard = createCardKeyboard();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Доступные карты:");
        message.setReplyMarkup(keyboard);

        try {
            telegramBotmain.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCardSelection(long chatId, int cardIndex) {
        if (isFinished) return;


        System.out.println("Выбрана карта: " + cardIndex + ", Текущая сумма: " + currentSum + ", Осталось ходов: " + movesLeft);

        CardConstruct card = cardMap.get(cardIndex);
        if (card == null) {
            telegramBotmain.sendMessage(chatId, "Эта карта уже была выбрана. Пожалуйста, выберите другую.");
            return;
        }

        // Удаляем карту из карты
        cardMap.remove(cardIndex);

        // Учитываем значение карты
        if (card.isRed()) {
            currentSum += card.getValue();
        } else {
            currentSum -= card.getValue();
        }

        movesLeft--;


        // Проверяем состояние игры
        if (currentSum == targetNumber && movesLeft == 0) {
            isFinished = true;
            telegramBotmain.sendMessage(chatId, "Поздравляем! Вы достигли числа " + targetNumber + " и победили!");
        } else if (movesLeft == 0) {
            isFinished = true;
            telegramBotmain.sendMessage(chatId, "Игра окончена. Вы не достигли числа " + targetNumber + ". Ваш итог: " + currentSum);
        } else {
            telegramBotmain.sendMessage(chatId, "Текущая сумма: " + currentSum +
                    ". Осталось ходов: " + movesLeft + ". Выберите следующую карту.");
        }
    }


    @Override
    public void gameStarted(long chatId) {
        do {
            initializeCards(); // Генерация карт и установка целевого числа
        } while (!isTargetReachable(cardMap, targetNumber, movesLeft)); // Проверяем достижимость цели

        telegramBotmain.sendMessage(chatId, "Цель игры: достичь числа " + targetNumber +
                " за " + movesLeft + " ходов. Удачи!");
        sendCardKeyboard(chatId);
    }

    private boolean isTargetReachable(HashMap<Integer, CardConstruct> cardMap, int target, int movesLeft) {
        List<Integer> cardValues = new ArrayList<>();
        for (CardConstruct card : cardMap.values()) {
            int value = card.isRed() ? card.getValue() : -card.getValue(); // Красные прибавляют, черные вычитают
            cardValues.add(value);
        }

        return isCombinationPossible(cardValues, target, movesLeft);
    }

    private boolean isCombinationPossible(List<Integer> cardValues, int target, int movesLeft) {
        if (target == 0 && movesLeft == 0) return true; // Цель достигнута
        if (movesLeft == 0 || cardValues.isEmpty()) return false; // Нет ходов или карт

        // Перебираем карты
        for (int i = 0; i < cardValues.size(); i++) {
            int value = cardValues.get(i);

            // Создаем новую копию списка карт без текущей карты
            List<Integer> remainingCards = new ArrayList<>(cardValues);
            remainingCards.remove(i);

            // Рекурсивно проверяем комбинации
            if (isCombinationPossible(remainingCards, target - value, movesLeft - 1)) {
                return true;
            }
        }
        return false;
    }
    }