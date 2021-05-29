/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.casino.utils.Voltorb;

import com.m1zark.casino.utils.Voltorb.Card;

public class SumCard {
    private int voltorbs = 0;
    private int sum = 0;

    public void setTotals(Card[] cards) {
        for (Card card : cards) {
            if (card.getValue() == 1) continue;
            if (card.getValue() == 0) {
                ++this.voltorbs;
                continue;
            }
            this.sum += card.getValue();
        }
    }

    public int getVoltorbs() {
        return this.voltorbs;
    }

    public int getSum() {
        return this.sum;
    }
}

