/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.Event
 *  org.spongepowered.api.event.cause.Cause
 *  org.spongepowered.api.event.cause.EventContext
 *  org.spongepowered.api.event.cause.EventContextKeys
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.casino.utils;

import com.m1zark.casino.Casino;
import com.m1zark.casino.config.Cooldowns;
import com.m1zark.casino.config.MarketConfig;
import com.m1zark.casino.config.SlotsConfig;
import com.m1zark.casino.config.VoltorbFlipConfig;
import com.m1zark.casino.events.SlotsEvent;
import com.m1zark.casino.events.VoltorbEvent;
import com.m1zark.casino.utils.Rewards;
import com.m1zark.m1utilities.api.Chat;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.text.Text;

public class Utils {
    public static void processSlotsWinner(List<String> pokemon, Player player) {
        List distinctElements = pokemon.stream().distinct().collect(Collectors.toList());
        if (distinctElements.size() == 2) {
            List duplicates = pokemon.stream().collect(Collectors.groupingBy(Function.identity())).entrySet().stream().filter(e -> e.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
            if (SlotsConfig.hasRewards((String)duplicates.get(0), 2)) {
                int reward = SlotsConfig.getRewards((String)duplicates.get(0), 2);
                SlotsEvent slotEvent = new SlotsEvent(player.getName(), (String)duplicates.get(0), 2, reward, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Casino.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, player.getProfile()).build(), Casino.getInstance()));
                Sponge.getEventManager().post(slotEvent);
                if (!slotEvent.isCancelled()) {
                    Casino.getInstance().getSql().updateBalanceTotal(player.getUniqueId(), false, reward);
                    Chat.sendMessage(player, Chat.embedColours(SlotsConfig.getMessages("Messages.win").replace("{matches}", String.valueOf(2)).replace("{pokemon}", (CharSequence)duplicates.get(0)).replace("{reward}", reward + " " + MarketConfig.CURRENCY_NAME)));
                }
            } else {
                Chat.sendMessage(player, Chat.embedColours(SlotsConfig.getMessages("Messages.lose")));
            }
        } else if (distinctElements.size() == 1) {
            if (SlotsConfig.hasRewards((String)distinctElements.get(0), 3)) {
                int reward = SlotsConfig.getRewards((String)distinctElements.get(0), 3);
                SlotsEvent slotEvent = new SlotsEvent(player.getName(), (String)distinctElements.get(0), 2, reward, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Casino.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, player.getProfile()).build(), Casino.getInstance()));
                Sponge.getEventManager().post(slotEvent);
                if (!slotEvent.isCancelled()) {
                    Casino.getInstance().getSql().updateBalanceTotal(player.getUniqueId(), false, reward);
                    Chat.sendMessage(player, Chat.embedColours(SlotsConfig.getMessages("Messages.win").replace("{matches}", String.valueOf(3)).replace("{pokemon}", (CharSequence)distinctElements.get(0)).replace("{reward}", reward + " " + MarketConfig.CURRENCY_NAME)));
                }
            } else {
                Chat.sendMessage(player, Chat.embedColours(SlotsConfig.getMessages("Messages.lose")));
            }
        } else {
            Chat.sendMessage(player, Chat.embedColours(SlotsConfig.getMessages("Messages.lose")));
        }
    }

    public static boolean processVoltorbWinner(Player player, int score) {
        for (Rewards rewards : VoltorbFlipConfig.Rewards()) {
            if (Casino.getInstance().getVoltorbflip().get(player.getUniqueId()).contains(rewards) || !Utils.between(score, rewards.getCount(), rewards.getNextCount()) || !Cooldowns.saveCooldown("voltorb", player.getUniqueId(), Instant.now().plusSeconds(VoltorbFlipConfig.cooldown).toEpochMilli())) continue;
            VoltorbEvent voltorbEvent = new VoltorbEvent(player.getName(), score, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Casino.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, player.getProfile()).build(), Casino.getInstance()));
            Sponge.getEventManager().post(voltorbEvent);
            if (voltorbEvent.isCancelled()) continue;
            Casino.getInstance().getVoltorbflip().get(player.getUniqueId()).add(rewards);
            Casino.getInstance().getSql().updateBalanceTotal(player.getUniqueId(), false, rewards.getReward());
            Chat.sendMessage(player, VoltorbFlipConfig.getMessages("Messages.win").replace("{score}", String.valueOf(score)).replace("{reward}", rewards.getDisplay_name()));
            return true;
        }
        return false;
    }

    public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return i >= minValueInclusive && i <= maxValueInclusive;
    }
}

