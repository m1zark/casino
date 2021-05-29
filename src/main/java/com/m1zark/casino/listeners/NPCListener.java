/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.Money
 *  com.m1zark.m1utilities.api.Time
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.api.dialogue.Choice
 *  com.pixelmonmod.pixelmon.api.dialogue.Dialogue
 *  com.pixelmonmod.pixelmon.api.events.dialogue.DialogueChoiceEvent
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.api.storage.PCStorage
 *  com.pixelmonmod.pixelmon.entities.npcs.NPCChatting
 *  com.pixelmonmod.pixelmon.storage.PlayerPartyStorage
 *  net.minecraft.entity.player.EntityPlayerMP
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.entity.EntityType
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.event.Event
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.cause.Cause
 *  org.spongepowered.api.event.cause.EventContext
 *  org.spongepowered.api.event.cause.EventContextKeys
 *  org.spongepowered.api.event.entity.InteractEntityEvent$Secondary$MainHand
 *  org.spongepowered.api.event.filter.cause.First
 */
package com.m1zark.casino.listeners;

import com.google.common.collect.Lists;
import com.m1zark.casino.Casino;
import com.m1zark.casino.commands.Game;
import com.m1zark.casino.config.Cooldowns;
import com.m1zark.casino.config.LotteryConfig;
import com.m1zark.casino.config.MarketConfig;
import com.m1zark.casino.events.LotteryEvent;
import com.m1zark.casino.gui.MarketUI;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.Money;
import com.m1zark.m1utilities.api.Time;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.events.dialogue.DialogueChoiceEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.entities.npcs.NPCChatting;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

public class NPCListener {
    @Listener
    public void onNPCClick(InteractEntityEvent.Secondary.MainHand e, @First Player player) {
        EntityType et = Sponge.getRegistry().getType(EntityType.class, "pixelmon:chattingnpc").get();
        if (e.getTargetEntity().getType().equals(et)) {
            if (Game.lottery.getAdding().contains(player.getUniqueId())) {
                e.setCancelled(true);
                if (LotteryConfig.addNPCs(e.getTargetEntity().getUniqueId())) {
                    ((NPCChatting)e.getTargetEntity()).setName(LotteryConfig.npc_name);
                    ((NPCChatting)e.getTargetEntity()).setAlwaysRenderNameTag(true);
                    Game.lottery.getAdding().remove(player.getUniqueId());
                    Chat.sendMessage(player, "&7Lottery NPC has been registered!");
                }
            } else if (Game.market.getAdding().contains(player.getUniqueId())) {
                e.setCancelled(true);
                if (MarketConfig.addNPCs(e.getTargetEntity().getUniqueId())) {
                    ((NPCChatting)e.getTargetEntity()).setName(MarketConfig.NPC_NAME);
                    ((NPCChatting)e.getTargetEntity()).setAlwaysRenderNameTag(true);
                    Game.market.getAdding().remove(player.getUniqueId());
                    Chat.sendMessage(player, "&7Market NPC has been registered!");
                }
            } else if (Game.market.getRemoving().contains(player.getUniqueId())) {
                e.setCancelled(true);
                if (MarketConfig.removeNPCs(e.getTargetEntity().getUniqueId())) {
                    ((NPCChatting)e.getTargetEntity()).setName("");
                    Game.market.getRemoving().remove(player.getUniqueId());
                    Chat.sendMessage(player, "&7Market NPC has been removed!");
                }
            } else if (Game.lottery.getRemoving().contains(player.getUniqueId())) {
                e.setCancelled(true);
                if (LotteryConfig.removeNPCs(e.getTargetEntity().getUniqueId())) {
                    ((NPCChatting)e.getTargetEntity()).setName("");
                    Game.lottery.getRemoving().remove(player.getUniqueId());
                    Chat.sendMessage(player, "&7Lottery NPC has been removed!");
                }
            } else if (!LotteryConfig.getNPCs().isEmpty() && LotteryConfig.getNPCs().contains(e.getTargetEntity().getUniqueId().toString())) {
                e.setCancelled(true);
                Dialogue.setPlayerDialogueData((EntityPlayerMP) player, this.forgeDialogue(player, ((NPCChatting)e.getTargetEntity()).getName()), true);
            } else if (!MarketConfig.getNPCs().isEmpty() && MarketConfig.getNPCs().contains(e.getTargetEntity().getUniqueId().toString())) {
                e.setCancelled(true);
                player.openInventory(new MarketUI(player).getInventory());
            }
        }
    }

    private ArrayList<Dialogue> forgeDialogue(Player player, String name) {
        ArrayList<Dialogue> prompt = Lists.newArrayList();
        for (String text : LotteryConfig.getMessageLists("Messages.npc.dialogue.welcome-message")) {
            prompt.add(Dialogue.builder().setName(name).setText(text.replace("{player}", player.getName())).build());
        }
        prompt.add(Dialogue.builder().setName(name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.prompt.welcome-question").replace("{cost}", String.valueOf(LotteryConfig.cost))).addChoice(Choice.builder().setText("Sure, why not.").setHandle(e -> {
            String id = new StringBuilder(this.getRandomNumber()).reverse().toString();
            if (Cooldowns.getCooldown("lottery").containsKey(player.getUniqueId().toString())) {
                Time time = new Time(Cooldowns.getCooldown("lottery").get(player.getUniqueId().toString()));
                String expires = time.toString("%dd %dh %dm %ds");
                if (!expires.equalsIgnoreCase("Expired")) {
                    e.reply(Dialogue.builder().setName(name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.already-drawn").replace("{time}", expires)).build());
                } else if (Money.canPay(player, LotteryConfig.cost)) {
                    Money.withdrawn(player, LotteryConfig.cost);
                    e.reply(Dialogue.builder().setName(name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.prompt.yes").replace("{number}", id)).build());
                    this.checkLoto((EntityPlayerMP)player, id, e);
                } else {
                    e.reply(Dialogue.builder().setName(name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.no-funds").replace("{cost}", String.valueOf(LotteryConfig.cost))).build());
                }
            } else if (Money.canPay(player, LotteryConfig.cost)) {
                Money.withdrawn(player, LotteryConfig.cost);
                e.reply(Dialogue.builder().setName(name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.prompt.yes").replace("{number}", id)).build());
                this.checkLoto((EntityPlayerMP)player, id, e);
            } else {
                e.reply(Dialogue.builder().setName(name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.no-funds").replace("{cost}", String.valueOf(LotteryConfig.cost))).build());
            }
        }).build()).addChoice(Choice.builder().setText("I think I'll pass.").setHandle(e -> e.reply(Dialogue.builder().setName(name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.prompt.no")).build())).build()).build());
        return prompt;
    }

    private boolean checkLoto(EntityPlayerMP player, String number, DialogueChoiceEvent event) {
        if (Cooldowns.saveCooldown("lottery", player.getUniqueID(), Instant.now().plusSeconds(LotteryConfig.cooldown).toEpochMilli())) {
            int slot = 0;
            boolean pcCheck = false;
            PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueID());
            int[] match = new int[party.getTeam().size()];
            for (Pokemon pokemon : party.getTeam()) {
                if (pokemon == null || pokemon.isEgg()) continue;
                String id = String.valueOf(pokemon.getUUID().getMostSignificantBits());
                String pid = new StringBuilder(id.substring(id.length() - 5)).reverse().toString();
                if (LotteryConfig.debug) {
                    System.out.println("Loto: " + number + " Pokemon: " + pid);
                }
                match[slot] = this.checkMatches(number, pid);
                ++slot;
            }
            Arrays.sort(match);
            if (LotteryConfig.check_pc && match[match.length - 1] == 0) {
                PCStorage pc = Pixelmon.storageManager.getPCForPlayer(player.getUniqueID());
                slot = 0;
                match = new int[pc.getAll().length];
                pcCheck = true;
                for (Pokemon pokemon : pc.getAll()) {
                    if (pokemon == null || pokemon.isEgg()) continue;
                    String id = String.valueOf(pokemon.getUUID().getMostSignificantBits());
                    String pid = new StringBuilder(id.substring(id.length() - 5)).reverse().toString();
                    if (LotteryConfig.debug) {
                        System.out.println("Loto: " + number + " Pokemon: " + pid);
                    }
                    match[slot] = this.checkMatches(number, pid);
                    ++slot;
                }
            }
            Arrays.sort(match);
            if (LotteryConfig.debug) {
                System.out.println(Arrays.toString(match));
            }
            if (match[match.length - 1] > 0) {
                if (pcCheck) {
                    event.reply(Dialogue.builder().setName(LotteryConfig.npc_name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.win.in-pc")).build());
                } else {
                    event.reply(Dialogue.builder().setName(LotteryConfig.npc_name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.win.in-party")).build());
                }
                switch (match[match.length - 1]) {
                    case 1: {
                        event.reply(Dialogue.builder().setName(LotteryConfig.npc_name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.win.match.1-digit").replace("{prize}", LotteryConfig.reward_1 + " " + MarketConfig.CURRENCY_NAME)).build());
                        Casino.getInstance().getSql().updateBalanceTotal(player.getUniqueID(), false, LotteryConfig.reward_1);
                        Sponge.getEventManager().post(new LotteryEvent(player.getName(), 1, LotteryConfig.reward_1, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Casino.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, ((Player)player).getProfile()).build(), Casino.getInstance())));
                        break;
                    }
                    case 2: {
                        event.reply(Dialogue.builder().setName(LotteryConfig.npc_name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.win.match.1-digit").replace("{prize}", LotteryConfig.reward_2 + " " + MarketConfig.CURRENCY_NAME)).build());
                        Casino.getInstance().getSql().updateBalanceTotal(player.getUniqueID(), false, LotteryConfig.reward_2);
                        Sponge.getEventManager().post(new LotteryEvent(player.getName(), 1, LotteryConfig.reward_2, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Casino.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, ((Player)player).getProfile()).build(), Casino.getInstance())));
                        break;
                    }
                    case 3: {
                        event.reply(Dialogue.builder().setName(LotteryConfig.npc_name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.win.match.1-digit").replace("{prize}", LotteryConfig.reward_3 + " " + MarketConfig.CURRENCY_NAME)).build());
                        Casino.getInstance().getSql().updateBalanceTotal(player.getUniqueID(), false, LotteryConfig.reward_3);
                        Sponge.getEventManager().post(new LotteryEvent(player.getName(), 1, LotteryConfig.reward_3, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Casino.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, ((Player)player).getProfile()).build(), Casino.getInstance())));
                        break;
                    }
                    case 4: {
                        event.reply(Dialogue.builder().setName(LotteryConfig.npc_name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.win.match.1-digit").replace("{prize}", LotteryConfig.reward_4 + " " + MarketConfig.CURRENCY_NAME)).build());
                        Casino.getInstance().getSql().updateBalanceTotal(player.getUniqueID(), false, LotteryConfig.reward_4);
                        Sponge.getEventManager().post(new LotteryEvent(player.getName(), 1, LotteryConfig.reward_4, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Casino.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, ((Player)player).getProfile()).build(), Casino.getInstance())));
                        break;
                    }
                    case 5: {
                        event.reply(Dialogue.builder().setName(LotteryConfig.npc_name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.win.match.1-digit").replace("{prize}", LotteryConfig.reward_5 + " " + MarketConfig.CURRENCY_NAME)).build());
                        Casino.getInstance().getSql().updateBalanceTotal(player.getUniqueID(), false, LotteryConfig.reward_5);
                        Sponge.getEventManager().post(new LotteryEvent(player.getName(), 1, LotteryConfig.reward_5, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, Casino.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, ((Player)player).getProfile()).build(), Casino.getInstance())));
                    }
                }
            } else {
                event.reply(Dialogue.builder().setName(LotteryConfig.npc_name).setText(LotteryConfig.getMessages("Messages.npc.dialogue.lose")).build());
            }
        } else {
            Chat.sendMessage((Player)player, "&cThere was an error saving lottery data. Please notify staff.");
        }
        return true;
    }

    private String getRandomNumber() {
        String id = String.valueOf(UUID.randomUUID().getMostSignificantBits());
        return id.substring(id.length() - 5);
    }

    private int checkMatches(String n1, String n2) {
        int count = 0;
        if (n1.charAt(0) == n2.charAt(0)) {
            ++count;
            if (n1.charAt(1) == n2.charAt(1)) {
                ++count;
                if (n1.charAt(2) == n2.charAt(2)) {
                    ++count;
                    if (n1.charAt(3) == n2.charAt(3)) {
                        ++count;
                        if (n1.charAt(4) == n2.charAt(4)) {
                            ++count;
                        }
                    }
                }
            }
        }
        return count;
    }
}

