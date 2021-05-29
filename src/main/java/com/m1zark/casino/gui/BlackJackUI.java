/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.casino.gui;

import com.m1zark.casino.utils.BlackJack.BlackJack;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class BlackJackUI
extends InventoryManager {
    private Player player;
    private BlackJack game;

    public BlackJackUI(Player p) {
        super(p, 3, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lCasino &7\u00bb &8BlackJack")}));
        this.player = p;
        this.setupInventory();
    }

    private void setupInventory() {
    }

    private void playerCards() {
    }

    private void dealerCards() {
    }
}

