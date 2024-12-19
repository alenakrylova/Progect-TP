package org.example.bot;

public interface GameHandler {
    void handleInput(long chatId, String input);
    void gameStarted(long chatId);
}
