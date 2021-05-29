/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.casino.utils.BlackJack;

import com.m1zark.casino.utils.BlackJack.Card;
import java.util.List;

public class Hand {
    public static int[] getValues(List<Card> list) {
        int count1 = 0;
        for (Card x : list) {
            count1 += x.getValue();
        }
        int count2 = 0;
        boolean usedEleven = false;
        for (Card y : list) {
            if (y.getValue() == 1 && !usedEleven) {
                count2 += 11;
                usedEleven = true;
                continue;
            }
            count2 += y.getValue();
        }
        if (count2 > 21) {
            count2 = count1;
        }
        return new int[]{count1, count2};
    }
}

