/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  com.m1zark.m1utilities.api.Time
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.enums.EnumSpecies
 *  com.pixelmonmod.pixelmon.items.ItemPixelmonSprite
 *  net.minecraft.item.ItemStack
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.data.type.DyeColor
 *  org.spongepowered.api.data.type.DyeColors
 *  org.spongepowered.api.effect.sound.SoundTypes
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Primary
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Secondary
 *  org.spongepowered.api.item.ItemType
 *  org.spongepowered.api.item.ItemTypes
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 *  org.spongepowered.api.text.format.TextStyles
 *  org.spongepowered.common.item.inventory.util.ItemStackUtil
 */
package com.m1zark.casino.gui;

import com.google.common.collect.Lists;
import com.m1zark.casino.Casino;
import com.m1zark.casino.config.Cooldowns;
import com.m1zark.casino.config.VoltorbFlipConfig;
import com.m1zark.casino.utils.Utils;
import com.m1zark.casino.utils.Voltorb.VoltorbBoard;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.Time;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import java.util.ArrayList;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

public class VoltorbUI
extends InventoryManager {
    private VoltorbBoard board;
    private int level;
    private int totalScore;

    public VoltorbUI(Player player) {
        super(player, 6, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lCasino &7\u00bb &8Voltorb Flip")}));
        this.player = player;
        this.level = 1;
        this.totalScore = 0;
        if (!Casino.getInstance().getVoltorbflip().containsKey(player.getUniqueId())) {
            Casino.getInstance().getVoltorbflip().put(player.getUniqueId(), Lists.newArrayList());
        }
        this.setupBoard();
        this.setupInventory();
    }

    private void setupInventory() {
        int x = 0;
        int y = 0;
        int index = 0;
        y = 5;
        while (x < 5) {
            this.addIcon(this.sumsIcon(x + 9 * y, index, 0));
            ++x;
            ++index;
        }
        index = 0;
        x = 5;
        y = 0;
        while (y < 5) {
            this.addIcon(this.sumsIcon(x + 9 * y, index, 1));
            ++y;
            ++index;
        }
        this.addIcon(this.scoreIcon());
        for (y = 0; y < 5; ++y) {
            for (x = 0; x < 5; ++x) {
                this.addIcon(this.cardIcon(x + 9 * y, DyeColors.GREEN, 1, false, false));
            }
        }
    }

    private void setupBoard() {
        this.board = new VoltorbBoard(this.level);
        this.board.initBoard();
    }

    private Icon voltorbIcon(int slot) {
        ItemStack item = ItemStackUtil.fromNative((net.minecraft.item.ItemStack)ItemPixelmonSprite.getPhoto((Pokemon)Pixelmon.pokemonFactory.create(EnumSpecies.Voltorb)));
        item.offer(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, "Voltorb!"}));
        return new Icon(slot, item);
    }

    private Icon cardIcon(int slot, DyeColor color, int value, boolean clicked, boolean marked) {
        Icon icon = new Icon(slot, ItemStack.builder().itemType(marked ? ItemTypes.BANNER : ItemTypes.STAINED_GLASS_PANE).quantity(value == 0 ? 1 : value).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{Chat.embedColours((String)(clicked ? "&6x" + value : (marked ? "&6Flagged" : "&6Click to flip")))})).add(Keys.DYE_COLOR, color).build());
        icon.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                if (!(clicked || this.board.checkBoard() || this.board.isGameOver())) {
                    int score = this.board.getBoard().get(slot);
                    this.clearIcons(slot);
                    switch (score) {
                        case 0: {
                            this.addIcon(this.voltorbIcon(slot));
                            this.player.playSound(SoundTypes.ENTITY_FIREWORK_LARGE_BLAST, this.player.getLocation().getPosition(), 2.0);
                            this.board.setGameOver(true);
                            break;
                        }
                        case 1: {
                            this.addIcon(this.cardIcon(slot, DyeColors.WHITE, score, true, false));
                            break;
                        }
                        case 2: 
                        case 3: {
                            this.addIcon(this.cardIcon(slot, DyeColors.RED, score, true, false));
                            this.board.setCardsFlipped();
                        }
                    }
                    this.board.setScore(score);
                    this.updateContents(new int[]{slot});
                    if (this.board.isGameOver() || this.board.checkBoard()) {
                        this.processGame();
                    }
                    this.clearIcons(new int[]{16});
                    this.addIcon(this.scoreIcon());
                    this.updateContents(new int[]{16});
                }
            } else if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary && !clicked && !this.board.checkBoard() && !this.board.isGameOver()) {
                this.clearIcons(new int[]{slot});
                this.addIcon(this.cardIcon(slot, marked ? DyeColors.GREEN : DyeColors.YELLOW, 1, false, !marked));
                this.updateContents(new int[]{slot});
            }
        }).delayTicks(1L).submit((Object)Casino.getInstance()));
        return icon;
    }

    private Icon sumsIcon(int slot, int color, int index) {
        ArrayList<DyeColor> colors = Lists.newArrayList(DyeColors.RED, DyeColors.LIME, DyeColors.ORANGE, DyeColors.BLUE, DyeColors.MAGENTA);
        ArrayList<String> items = Lists.newArrayList("pixelmon:flame_plate", "pixelmon:meadow_plate", "pixelmon:zap_plate", "pixelmon:splash_plate", "pixelmon:toxic_plate");
        int multiplier = this.board.getSumCards()[index][color].getSum();
        int voltorb = this.board.getSumCards()[index][color].getVoltorbs();
        return new Icon(slot, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, (String)items.get(color)).orElse(ItemTypes.CONCRETE)).add(Keys.DYE_COLOR, colors.get(color)).add(Keys.DISPLAY_NAME, Text.of((String)"")).add(Keys.ITEM_LORE, Lists.newArrayList(Text.of((Object[])new Object[]{Chat.embedColours((String)("&aMultipliers: &b" + multiplier))}), Text.of((Object[])new Object[]{Chat.embedColours((String)("&aVoltorbs: &b" + voltorb))}))).build());
    }

    private Icon scoreIcon() {
        ArrayList<Text> itemLore = Lists.newArrayList();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&aTotal Score: &b" + this.totalScore))}));
        itemLore.add(Text.EMPTY);
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&aCurrent Score: &b" + this.board.getScore()))}));
        return new Icon(16, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:weakness_policy").orElse(ItemTypes.CONCRETE)).add(Keys.DYE_COLOR, DyeColors.WHITE).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, TextStyles.BOLD, "Voltorb Flip: Level " + this.board.getLevel()})).add(Keys.ITEM_LORE, itemLore).build());
    }

    private Icon winloseIcon() {
        ArrayList<Text> itemLore = Lists.newArrayList();
        if (this.board.isGameOver()) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bOh tough break... looks like you lost this round.")}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&bClick here to try again... you will be taken back to level &a" + this.level))}));
        } else if (this.totalScore == VoltorbFlipConfig.maxScore) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bCongrats you reached the highest score!")}));
        } else if (this.board.checkBoard()) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&bYou beat Level &a" + this.board.getLevel() + "!"))}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&bClick here to move onto level &a" + this.level))}));
        }
        if (VoltorbFlipConfig.hasRewards(this.totalScore, this.player)) {
            if (Cooldowns.getCooldown("voltorb").containsKey(this.player.getUniqueId().toString())) {
                Time time = new Time(Cooldowns.getCooldown("voltorb").get(this.player.getUniqueId().toString()).longValue());
                String expires = time.toString("%dd %dh %dm %ds");
                if (!expires.equalsIgnoreCase("Expired")) {
                    itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
                    itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bYou have already collected a reward.")}));
                    itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&bYou can claim again in &a" + expires + "&b."))}));
                } else {
                    itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
                    itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&bThere are rewards available for a score of &a" + this.totalScore + "&b."))}));
                    itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bRight-click here to collect your reward.")}));
                    itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
                    itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&cNote: Once you collect a reward you will have to start from the beginning.")}));
                }
            } else {
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&bThere are rewards available for a score of &a" + this.totalScore + "&b."))}));
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bRight-click here to collect your reward.")}));
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&cNote: Once you collect a reward you will have to start from the beginning.")}));
            }
        }
        ItemStack item = ItemStack.builder().itemType(ItemTypes.CONCRETE).add(Keys.DYE_COLOR, (this.board.isGameOver() ? DyeColors.BLACK : DyeColors.GREEN)).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, TextStyles.BOLD, "Voltorb Flip"})).add(Keys.ITEM_LORE, itemLore).build();
        Icon icon = new Icon(34, item);
        icon.addListener(clickable -> {
            if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                if (this.totalScore != VoltorbFlipConfig.maxScore) {
                    this.clearIcons(new int[0]);
                    this.removeIcon(34);
                    this.setupBoard();
                    this.setupInventory();
                    this.updateContents();
                }
            } else if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary) {
                if (Cooldowns.getCooldown("voltorb").containsKey(this.player.getUniqueId().toString())) {
                    Time time = new Time(Cooldowns.getCooldown("voltorb").get(this.player.getUniqueId().toString()).longValue());
                    String expires = time.toString("%dd %dh %dm %ds");
                    if (!expires.equalsIgnoreCase("Expired")) {
                        Chat.sendMessage((CommandSource)this.player, (String)VoltorbFlipConfig.getMessages("Messages.reward-cooldown").replace("{time}", expires));
                    } else if (Utils.processVoltorbWinner(this.player, this.totalScore)) {
                        this.player.closeInventory();
                    }
                } else if (Utils.processVoltorbWinner(this.player, this.totalScore)) {
                    this.player.closeInventory();
                }
            }
        });
        return icon;
    }

    private void processGame() {
        if (this.board.checkBoard()) {
            this.totalScore += this.board.getScore();
            this.level = this.level >= VoltorbFlipConfig.maxLevel ? VoltorbFlipConfig.maxLevel : this.level + 1;
            this.addIcon(this.winloseIcon());
            this.updateContents(new int[]{34});
        } else if (this.board.isGameOver()) {
            this.level = this.level < 3 ? 1 : this.level - 2;
            this.addIcon(this.winloseIcon());
            this.updateContents(new int[]{34});
        }
    }
}

