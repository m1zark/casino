/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.spongepowered.api.event.cause.Cause
 */
package com.m1zark.casino.events;

import com.m1zark.casino.events.BaseEvent;
import lombok.NonNull;
import org.spongepowered.api.event.cause.Cause;

public class SlotsEvent
extends BaseEvent {
    private final String player;
    private final String pokemon;
    private final int count;
    private final int reward;
    @NonNull
    private final Cause cause;

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public String getPlayer() {
        return this.player;
    }

    public String getPokemon() {
        return this.pokemon;
    }

    public int getCount() {
        return this.count;
    }

    public int getReward() {
        return this.reward;
    }

    public SlotsEvent(String player, String pokemon, int count, int reward, @NonNull Cause cause) {
        if (cause == null) {
            throw new NullPointerException("cause is marked non-null but is null");
        }
        this.player = player;
        this.pokemon = pokemon;
        this.count = count;
        this.reward = reward;
        this.cause = cause;
    }
}

