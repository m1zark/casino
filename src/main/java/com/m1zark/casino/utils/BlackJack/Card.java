/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.casino.utils.BlackJack;

public class Card {
    private int value;
    private String name;
    private String suite;
    private boolean Ace;

    public Card(String name, String suite) {
        this.name = name;
        this.suite = suite;
        this.value = this.determineCardValue(name);
    }

    public void printCard() {
        System.out.println(this.name + " of " + this.suite);
    }

    public int getValue() {
        return this.value;
    }

    public boolean isAce() {
        return this.Ace;
    }

    private int determineCardValue(String name) throws NumberFormatException {
        int value = 0;
        try {
            value = Integer.parseInt(name.substring(0, 1));
            return value;
        }
        catch (NumberFormatException e) {
            if (name.charAt(0) == 'K' || name.charAt(0) == 'J' || name.charAt(0) == 'Q' || name.charAt(0) == '0') {
                value = 10;
            } else if (name.charAt(0) == 'A') {
                value = 1;
                this.Ace = true;
            } else {
                value = Integer.parseInt(name.substring(0, 1));
            }
            return value;
        }
    }
}

