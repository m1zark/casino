/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.casino.commands;

import com.google.common.collect.Lists;
import com.m1zark.casino.Casino;
import com.m1zark.casino.config.LotteryConfig;
import com.m1zark.casino.config.MarketConfig;
import com.m1zark.casino.config.SlotsConfig;
import com.m1zark.casino.config.VoltorbFlipConfig;
import com.m1zark.casino.utils.Rewards;
import com.m1zark.m1utilities.api.Chat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Game
implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "You must be logged onto the server to run this command."));
        }
        Optional<String> type = args.getOne("type");
        type.ifPresent(t -> {
            switch (t) {
                case "slots": {
                    Chat.sendMessage(src, "&aIt's a slot machine... it does slot machine stuff.");
                    Chat.sendMessage(src, "&6-=Rewards=-");
                    SlotsConfig.getAllRewards().forEach(r -> Chat.sendMessage(src, Text.of(Chat.embedColours(("&7" + r.getPokemon() + " x" + r.getCount() + "&7 -> &b" + r.getDisplay_name())))));
                    break;
                }
                case "voltorbflip": {
                    Chat.sendMessage((CommandSource)src, (String)"&aIt features a five-by-five grid of tiles, underneath of which are hidden numbers (multiplier cards which affect the player's score) and Voltorb (which ends the game). ");
                    Chat.sendMessage((CommandSource)src, (String)"&aFlipping a multiplier card will give the player that score on the first card, or multiply the total by the number flipped for all subsequent flips. Higher levels have more multiplier cards, therefore producing larger payouts. Flipping a Voltorb will cause the player to lose all coins earned during the current string of multipliers.");
                    Chat.sendMessage((CommandSource)src, (String)"&aThe player wins and advances one level by finding all of the x2 and x3 multiplier cards. The only way to advance to higher levels is to win the round; level 8 is achieved by winning five games in a row of any level. Exiting the game will reset the player to level 1.");
                    Chat.sendMessage((CommandSource)src, (String)"&6-=Rewards=-");
                    List<Rewards> rewards = VoltorbFlipConfig.Rewards();
                    rewards.sort(Comparator.comparing(Rewards::getCount));
                    rewards.forEach(r -> Chat.sendMessage((CommandSource)src, (Text)Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Score of " + r.getCount() + "&7 -> &b" + r.getDisplay_name()))})));
                    break;
                }
                case "lottery": {
                    Chat.sendMessage(src, "&aIf you have a Pok\u00e9mon with the same or a similar ID as your loto number, they will receive a prize. In order for a Pok\u00e9mon's ID to be eligible for an award, one or more consecutive corresponding digits of the Pok\u00e9mon's ID and the loto ID must be the same.");
                    Chat.sendMessage(src, "&6-=Rewards=-");
                    Chat.sendMessage(src, ("&71 Digit&7: &b" + LotteryConfig.reward_1 + " " + MarketConfig.CURRENCY_NAME));
                    Chat.sendMessage(src, ("&72 Digits&7: &b" + LotteryConfig.reward_2 + " " + MarketConfig.CURRENCY_NAME));
                    Chat.sendMessage(src, ("&73 Digits&7: &b" + LotteryConfig.reward_3 + " " + MarketConfig.CURRENCY_NAME));
                    Chat.sendMessage(src, ("&74 Digits&7: &b" + LotteryConfig.reward_4 + " " + MarketConfig.CURRENCY_NAME));
                    Chat.sendMessage(src, ("&75 Digits&7: &b" + LotteryConfig.reward_5 + " " + MarketConfig.CURRENCY_NAME));
                }
            }
        });
        return CommandResult.success();
    }

    public static class balance implements CommandExecutor {
        private static List<UUID> adding = Lists.newArrayList();

        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (!(src instanceof Player)) {
                throw new CommandException(Text.of(TextColors.RED, "You must be logged onto the server to run this command."));
            }

            Optional<Integer> amount = args.getOne("amount");
            Optional<Player> player = args.getOne("player");
            Casino.getInstance().getSql().updateBalanceTotal(player.orElse((Player)src).getUniqueId(), false, amount.orElse(0));
            Chat.sendMessage(src, ("&7Successfully gave " + player.orElse((Player)src).getName() + " " + amount.orElse(0) + " " + MarketConfig.CURRENCY_NAME + "."));
            return CommandResult.success();
        }

        public static List<UUID> getAdding() {
            return adding;
        }
    }

    public static class market implements CommandExecutor {
        private static List<UUID> adding = Lists.newArrayList();
        private static List<UUID> removing = Lists.newArrayList();

        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (!(src instanceof Player)) {
                throw new CommandException(Text.of(TextColors.RED, "You must be logged onto the server to run this command."));
            }
            Optional<String> type = args.getOne("type");
            if (!adding.contains(((Player)src).getUniqueId()) || !removing.contains(((Player)src).getUniqueId())) {
                type.ifPresent(t -> {
                    if (t.equals("set") && !adding.contains(((Player)src).getUniqueId())) {
                        adding.add(((Player)src).getUniqueId());
                        Chat.sendMessage(src, "&7Right click a chatting npc to register them as a casino market npc!");
                    } else if (t.equals("delete") && !removing.contains(((Player)src).getUniqueId())) {
                        removing.add(((Player)src).getUniqueId());
                        Chat.sendMessage(src, "&7Right click a chatting npc to remove them as a casino market npc!");
                    } else {
                        Chat.sendMessage(src, "&7You are already registered to add a market npc.");
                    }
                });
            } else {
                Chat.sendMessage(src, "&7You are already registered to add a market npc.");
            }
            return CommandResult.success();
        }

        public static List<UUID> getAdding() {
            return adding;
        }

        public static List<UUID> getRemoving() {
            return removing;
        }
    }

    public static class lottery implements CommandExecutor {
        private static List<UUID> adding = Lists.newArrayList();
        private static List<UUID> removing = Lists.newArrayList();

        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (!(src instanceof Player)) {
                throw new CommandException(Text.of(TextColors.RED, "You must be logged onto the server to run this command."));
            }
            Optional<String> type = args.getOne("type");
            if (!adding.contains(((Player)src).getUniqueId()) || !removing.contains(((Player)src).getUniqueId())) {
                type.ifPresent(t -> {
                    if (t.equals("set") && !adding.contains(((Player)src).getUniqueId())) {
                        adding.add(((Player)src).getUniqueId());
                        Chat.sendMessage(src, "&7Right click a chatting npc to register them as a casino lottery npc!");
                    } else if (t.equals("delete") && !removing.contains(((Player)src).getUniqueId())) {
                        removing.add(((Player)src).getUniqueId());
                        Chat.sendMessage(src, "&7Right click a chatting npc to remove them as a casino lottery npc!");
                    } else {
                        Chat.sendMessage(src, "&7You are already registered to add a lottery npc.");
                    }
                });
            } else {
                Chat.sendMessage(src, "&7You are already registered to add a lottery npc.");
            }
            return CommandResult.success();
        }

        public static List<UUID> getAdding() {
            return adding;
        }

        public static List<UUID> getRemoving() {
            return removing;
        }
    }

    public static class sign implements CommandExecutor {
        private static HashMap<UUID, String> adding = new HashMap();

        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (!(src instanceof Player)) {
                throw new CommandException(Text.of(TextColors.RED, "You must be logged onto the server to run this command."));
            }
            Optional<String> type = args.getOne("type");
            if (!adding.containsKey(((Player)src).getUniqueId())) {
                type.ifPresent(t -> {
                    if (t.equals("slots")) {
                        adding.put(((Player)src).getUniqueId(), "slots");
                        Chat.sendMessage(src, "&7Right click a sign to register it as a slot machine. Sneak + Right click to remove a slot machine sign.");
                    }
                    if (t.equals("voltorbflip")) {
                        adding.put(((Player)src).getUniqueId(), "voltorbflip");
                        Chat.sendMessage(src, "&7Right click a sign to register it as a voltorb flip machine. Sneak + Right click to remove a voltorb flip machine sign.");
                    }
                });
            } else {
                Chat.sendMessage(src, ("&7You are already registered to add/remove a " + adding.get(((Player)src).getUniqueId()) + " sign."));
            }
            return CommandResult.success();
        }

        public static HashMap<UUID, String> getAdding() {
            return adding;
        }
    }
}

