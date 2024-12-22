package org.example.bot.games.helpers;

import java.util.HashMap;
import java.util.Random;

public class CardConstruct {
    public int value;
    public String suit;

    public CardConstruct(int value, String suit){
        this.value = value;
        this.suit = suit;
    }
    public CardConstruct(){

    }
    public void cartCreator(HashMap<Integer, CardConstruct> map, int i){
        int val = (int)(Math.random()*10) + 1;
        String[] suits = {"♥", "♠", "♦", "♣"};
        Random random = new Random();
        String sui = suits[random.nextInt(suits.length)];
        CardConstruct cuurent = new CardConstruct(val, sui);
        alrHave(map, i, cuurent);
    }


    @Override
    public String toString() {
        return value + suit;
    }

    public void alrHave(HashMap<Integer, CardConstruct> map, int i, CardConstruct currentCard){
        if (map.values().stream().anyMatch(card ->
                card.value == currentCard.value && card.suit == currentCard.suit)) {
            cartCreator(map, i);
        } else {
            map.put(i, currentCard);
        }
    }

    public boolean isRed() {
        return suit.equals("♥") || suit.equals("♦");
    }

    public int getValue() {
        return value;
    }
}
