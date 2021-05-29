/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.casino.utils;

public class Rewards {
    private final int count;
    private final int nextCount;
    private final int reward;
    private final String pokemon;
    private final String match;
    private final String display_name;

    public int getCount() {
        return this.count;
    }

    public int getNextCount() {
        return this.nextCount;
    }

    public int getReward() {
        return this.reward;
    }

    public String getPokemon() {
        return this.pokemon;
    }

    public String getMatch() {
        return this.match;
    }

    public String getDisplay_name() {
        return this.display_name;
    }

    public Rewards(int count, int nextCount, int reward, String pokemon, String match, String display_name) {
        this.count = count;
        this.nextCount = nextCount;
        this.reward = reward;
        this.pokemon = pokemon;
        this.match = match;
        this.display_name = display_name;
    }
}

