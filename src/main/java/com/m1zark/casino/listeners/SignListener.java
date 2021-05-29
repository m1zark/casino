/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.Money
 *  org.spongepowered.api.block.BlockType
 *  org.spongepowered.api.block.BlockTypes
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.data.type.HandTypes
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.block.InteractBlockEvent$Secondary
 *  org.spongepowered.api.event.filter.cause.Root
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 *  org.spongepowered.api.world.Location
 */
package com.m1zark.casino.listeners;

import com.google.common.collect.Lists;
import com.m1zark.casino.commands.Game;
import com.m1zark.casino.config.SlotsConfig;
import com.m1zark.casino.config.VoltorbFlipConfig;
import com.m1zark.casino.gui.SlotsUI;
import com.m1zark.casino.gui.VoltorbUI;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.Money;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

public class SignListener {
    @Listener
    public void onInteractBlock(InteractBlockEvent.Secondary event, @Root Player player) {
        if (!event.getTargetBlock().getLocation().isPresent()) {
            return;
        }
        if (!event.getHandType().equals(HandTypes.MAIN_HAND)) {
            return;
        }
        BlockType type = event.getTargetBlock().getState().getType();
        if (type != BlockTypes.STANDING_SIGN && type != BlockTypes.WALL_SIGN) {
            return;
        }
        Location loc = (Location)event.getTargetBlock().getLocation().get();
        Boolean sneak = player.get(Keys.IS_SNEAKING).orElse(false);
        if (player.hasPermission("casino.admin.signs") && Game.sign.getAdding().containsKey(player.getUniqueId())) {
            if (sneak.booleanValue()) {
                if (Game.sign.getAdding().get(player.getUniqueId()).equals("slots")) {
                    if (SlotsConfig.removeSignLocation(loc.getPosition().toString())) {
                        loc.offer(Keys.SIGN_LINES, (Object)Lists.newArrayList((Object[])new Text[]{Text.NEW_LINE, Text.NEW_LINE, Text.NEW_LINE}));
                        Chat.sendMessage((CommandSource)player, (Text)Chat.embedColours((String)"&7Slot sign removed"));
                    } else {
                        Chat.sendMessage((CommandSource)player, (Text)Chat.embedColours((String)"&cError removing slot sign. Check console."));
                    }
                } else if (Game.sign.getAdding().get(player.getUniqueId()).equals("voltorbflip")) {
                    if (VoltorbFlipConfig.removeSignLocation(loc.getPosition().toString())) {
                        loc.offer(Keys.SIGN_LINES, (Object)Lists.newArrayList((Object[])new Text[]{Text.NEW_LINE, Text.NEW_LINE, Text.NEW_LINE}));
                        Chat.sendMessage((CommandSource)player, (Text)Chat.embedColours((String)"&7Voltorb Flip sign removed"));
                    } else {
                        Chat.sendMessage((CommandSource)player, (Text)Chat.embedColours((String)"&cError removing voltorb flip sign. Check console."));
                    }
                }
            } else if (Game.sign.getAdding().get(player.getUniqueId()).equals("slots")) {
                if (SlotsConfig.addSignLocation(loc.getPosition().toString())) {
                    loc.offer(Keys.SIGN_LINES, (Object)Lists.newArrayList((Object[])new Text[]{Text.NEW_LINE, Text.of((Object[])new Object[]{TextColors.YELLOW, "[Slot Machine]"}), Text.of((Object[])new Object[]{TextColors.GREEN, "P" + SlotsConfig.spincost + " per spin"})}));
                    Chat.sendMessage((CommandSource)player, (Text)Chat.embedColours((String)"&7New slot sign created!"));
                } else {
                    Chat.sendMessage((CommandSource)player, (Text)Chat.embedColours((String)"&cError creating slot sign. Check console."));
                }
            } else if (Game.sign.getAdding().get(player.getUniqueId()).equals("voltorbflip")) {
                if (VoltorbFlipConfig.addSignLocation(loc.getPosition().toString())) {
                    loc.offer(Keys.SIGN_LINES, (Object)Lists.newArrayList((Object[])new Text[]{Text.NEW_LINE, Text.of((Object[])new Object[]{TextColors.YELLOW, "[Voltorb Flip]"}), Text.of((Object[])new Object[]{TextColors.GREEN, "Cost: P" + VoltorbFlipConfig.cost})}));
                    Chat.sendMessage((CommandSource)player, (Text)Chat.embedColours((String)"&7New voltorb flip sign created!"));
                } else {
                    Chat.sendMessage((CommandSource)player, (Text)Chat.embedColours((String)"&cError creating voltorb flip sign. Check console."));
                }
            }
            Game.sign.getAdding().remove(player.getUniqueId());
        } else {
            if (SlotsConfig.getSignLocations() != null && SlotsConfig.getSignLocations().contains(loc.getPosition().toString())) {
                player.openInventory(new SlotsUI(player).getInventory());
            }
            if (VoltorbFlipConfig.getSignLocations() != null && VoltorbFlipConfig.getSignLocations().contains(loc.getPosition().toString())) {
                if (VoltorbFlipConfig.cost != 0) {
                    if (Money.canPay((User)player, (int)VoltorbFlipConfig.cost)) {
                        Money.withdrawn((User)player, (int)VoltorbFlipConfig.cost);
                        player.openInventory(new VoltorbUI(player).getInventory());
                    } else {
                        Chat.sendMessage((CommandSource)player, (String)VoltorbFlipConfig.getMessages("Messages.not-enough-money"));
                    }
                } else {
                    player.openInventory(new VoltorbUI(player).getInventory());
                }
            }
        }
    }
}

