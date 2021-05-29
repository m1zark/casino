/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.casino.utils.Voltorb;

import com.m1zark.casino.config.VoltorbFlipConfig;
import com.m1zark.casino.utils.Voltorb.Card;
import com.m1zark.casino.utils.Voltorb.SumCard;
import java.util.HashMap;
import java.util.Random;

public class VoltorbBoard {
    Random random = new Random();
    private HashMap<Integer, Integer> board = new HashMap();
    private Card[][] cards = new Card[5][5];
    private SumCard[][] sumCards = new SumCard[2][5];
    private int level;
    private int cardsLeft = 0;
    private int cardsFlipped = 0;
    private int score = 1;
    private boolean gameOver = false;

    public VoltorbBoard(int level) {
        int j;
        int i;
        this.level = level;
        for (i = 0; i < this.cards.length; ++i) {
            for (j = 0; j < this.cards[i].length; ++j) {
                this.cards[i][j] = new Card();
            }
        }
        for (i = 0; i < this.sumCards.length; ++i) {
            for (j = 0; j < this.sumCards[i].length; ++j) {
                this.sumCards[i][j] = new SumCard();
            }
        }
    }

    public void initBoard() {
        this.initCards();
        this.initsumCards();
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                this.board.put(x + 9 * y, this.cards[x][y].getValue());
            }
        }
        if (VoltorbFlipConfig.debug) {
            System.out.println(this.board);
        }
    }

    private int[][] possibleSums(int n) {
        int start = (int)Math.ceil((double)n / -2.0);
        int end = (int)Math.floor((double)n / -3.0);
        int length = Math.abs(start - end) + 1;
        int[][] sums = new int[length][2];
        int j = 0;
        for (int i = start; i <= end; ++i) {
            sums[j][0] = -n - 3 * i;
            sums[j][1] = n + 2 * i;
            ++j;
        }
        return sums;
    }

    private void initCards() {
        int[] multiplierCards = new int[3];
        multiplierCards[0] = this.level * 3 / 2 + 5;
        if (multiplierCards[0] > 13) {
            multiplierCards[0] = 13;
        }
        int[][] sums = this.possibleSums(this.level * 2 + 8);
        int r = this.random.nextInt(sums.length);
        multiplierCards[1] = sums[r][0];
        multiplierCards[2] = sums[r][1];
        this.cardsLeft = multiplierCards[1] + multiplierCards[2];
        for (int i = 0; i < multiplierCards.length; ++i) {
            int j = 0;
            while (j < multiplierCards[i]) {
                int b;
                int a = this.random.nextInt(5);
                if (this.cards[a][b = this.random.nextInt(5)].getValue() != 1) continue;
                if (i == 0) {
                    this.cards[a][b].setValue(i);
                } else {
                    this.cards[a][b].setValue(i + 1);
                }
                ++j;
            }
        }
    }

    private void initsumCards() {
        Card[] column = new Card[5];
        for (int i = 0; i < this.cards.length; ++i) {
            this.sumCards[0][i].setTotals(this.cards[i]);
            for (int j = 0; j < this.cards[i].length; ++j) {
                column[j] = this.cards[j][i];
            }
            this.sumCards[1][i].setTotals(column);
        }
    }

    public boolean checkBoard() {
        return this.cardsFlipped == this.cardsLeft;
    }

    public void setScore(int s) {
        this.score = s == 0 ? 1 : this.score * s;
    }

    public void setCardsFlipped() {
        ++this.cardsFlipped;
    }

    public Random getRandom() {
        return this.random;
    }

    public HashMap<Integer, Integer> getBoard() {
        return this.board;
    }

    public Card[][] getCards() {
        return this.cards;
    }

    public SumCard[][] getSumCards() {
        return this.sumCards;
    }

    public int getLevel() {
        return this.level;
    }

    public int getCardsLeft() {
        return this.cardsLeft;
    }

    public int getCardsFlipped() {
        return this.cardsFlipped;
    }

    public int getScore() {
        return this.score;
    }

    public boolean isGameOver() {
        return this.gameOver;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setBoard(HashMap<Integer, Integer> board) {
        this.board = board;
    }

    public void setCards(Card[][] cards) {
        this.cards = cards;
    }

    public void setSumCards(SumCard[][] sumCards) {
        this.sumCards = sumCards;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCardsLeft(int cardsLeft) {
        this.cardsLeft = cardsLeft;
    }

    public void setCardsFlipped(int cardsFlipped) {
        this.cardsFlipped = cardsFlipped;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}

