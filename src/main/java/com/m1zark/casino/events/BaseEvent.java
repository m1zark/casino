/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.event.Cancellable
 *  org.spongepowered.api.event.cause.Cause
 *  org.spongepowered.api.event.impl.AbstractEvent
 */
package com.m1zark.casino.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public abstract class BaseEvent
extends AbstractEvent
implements Cancellable {
    private boolean cancelled = false;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public abstract Cause getCause();
}

