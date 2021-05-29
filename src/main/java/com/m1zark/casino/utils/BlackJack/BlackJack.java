/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.casino.utils.BlackJack;

import com.m1zark.casino.utils.BlackJack.Card;
import com.m1zark.casino.utils.BlackJack.Deck;
import com.m1zark.casino.utils.BlackJack.Hand;
import java.util.ArrayList;

public class BlackJack {
    public static Deck deck;
    public static int playerTotal;
    public static int dealerTotal;
    public static int bet;
    public static int dealerMinValue;
    public static int dealerMaxValue;
    public static int playerMinValue;
    public static int playerMaxValue;

    public BlackJack() {
        deck = new Deck();
    }

    public void setBet(int newBet) {
        bet = newBet;
    }

    public void initGame() {
        ArrayList<Card> playerCards = new ArrayList<Card>();
        ArrayList<Card> dealerCards = new ArrayList<Card>();
        deck.shuffleDeck();
        playerCards.add(deck.drawCard());
        playerCards.add(deck.drawCard());
        int[] playerHandVal = Hand.getValues(playerCards);
        playerMinValue = playerHandVal[0];
        playerMaxValue = playerHandVal[1];
        dealerCards.add(deck.drawCard());
        int[] dealerHandVal = Hand.getValues(dealerCards);
        dealerMinValue = dealerHandVal[0];
        dealerMaxValue = dealerHandVal[1];
    }

    static {
        playerTotal = 0;
        dealerTotal = 0;
        bet = 0;
        dealerMinValue = 0;
        dealerMaxValue = 0;
        playerMinValue = 0;
        playerMaxValue = 0;
    }
}

