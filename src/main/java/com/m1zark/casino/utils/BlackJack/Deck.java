/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.casino.utils.BlackJack;

import com.m1zark.casino.utils.BlackJack.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    private static final String[] suites = new String[]{"Hearts", "Spades", "Clubs", "Diamonds"};
    private static final String[] cards = new String[]{"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
    public List<Card> newDeck = new ArrayList<Card>();

    public Deck() {
        for (int i = 0; i < suites.length; ++i) {
            for (int j = 0; j < cards.length; ++j) {
                Card k = new Card(cards[j], suites[i]);
                this.newDeck.add(k);
            }
        }
    }

    public void shuffleDeck() {
        ArrayList<Card> shuffledDeck = new ArrayList<Card>();
        int r = 0;
        while (this.newDeck.size() > 0) {
            Random card = new Random();
            r = card.nextInt(this.newDeck.size());
            Card temp = this.newDeck.remove(r);
            shuffledDeck.add(temp);
        }
        this.newDeck = shuffledDeck;
    }

    public Card drawCard() {
        return this.newDeck.remove(0);
    }

    public List<Card> getNewDeck() {
        return this.newDeck;
    }
}

