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

public class VoltorbEvent
extends BaseEvent {
    private final String player;
    private final int score;
    @NonNull
    private final Cause cause;

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public String getPlayer() {
        return this.player;
    }

    public int getScore() {
        return this.score;
    }

    public VoltorbEvent(String player, int score, @NonNull Cause cause) {
        if (cause == null) {
            throw new NullPointerException("cause is marked non-null but is null");
        }
        this.player = player;
        this.score = score;
        this.cause = cause;
    }
}

