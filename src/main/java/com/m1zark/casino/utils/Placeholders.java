/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  me.rojo8399.placeholderapi.Placeholder
 *  me.rojo8399.placeholderapi.PlaceholderService
 *  me.rojo8399.placeholderapi.Source
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.entity.living.player.Player
 */
package com.m1zark.casino.utils;

import com.m1zark.casino.Casino;
import com.m1zark.casino.config.MarketConfig;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class Placeholders {
    private Placeholders() {
    }

    public static void register(Object plugin) {
        PlaceholderService placeholderService = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);
        placeholderService.loadAll(new Placeholders(), plugin).forEach(b -> {
            try {
                b.version("1.1.7-S7.2").author("m1zark").buildAndRegister();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Placeholder(id="casinobalance")
    public String casinoBalance(@Source Player player) {
        return Casino.getInstance().getSql().getBalance(player.getUniqueId()) + " " + MarketConfig.CURRENCY_NAME + "(s)";
    }
}

