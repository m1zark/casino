/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  com.m1zark.m1utilities.api.Money
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
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Primary
 *  org.spongepowered.api.item.ItemType
 *  org.spongepowered.api.item.ItemTypes
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.scheduler.Task
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 *  org.spongepowered.api.text.format.TextStyles
 *  org.spongepowered.common.item.inventory.util.ItemStackUtil
 */
package com.m1zark.casino.gui;

import com.m1zark.casino.Casino;
import com.m1zark.casino.config.SlotsConfig;
import com.m1zark.casino.utils.Utils;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.Money;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

public class SlotsUI
extends InventoryManager {
    private static final Random RANDOM = new Random();
    private Player player;
    private List<String> slots = new ArrayList<String>();
    private boolean spinning = false;

    public SlotsUI(Player p) {
        super(p, 3, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lCasino &7\u00bb &8Slots")}));
        this.player = p;
        this.setupInventory();
    }

    private void setupInventory() {
        int x;
        for (x = 0; x < 9; ++x) {
            this.addIcon(this.BorderIcon(x, DyeColors.RED, ""));
        }
        for (x = 9; x < 18; ++x) {
            this.addIcon(this.BorderIcon(x, DyeColors.BLACK, ""));
        }
        for (x = 18; x < 27; ++x) {
            this.addIcon(this.BorderIcon(x, DyeColors.WHITE, ""));
        }
        this.setupPokemonIcons();
        this.addIcon(this.spinIcon(14, this.player));
        this.addIcon(this.infoIcon(16));
    }

    private void setupPokemonIcons() {
        this.slots.clear();
        this.slots.add(0, SlotsConfig.pokemon.get(RANDOM.nextInt(SlotsConfig.pokemon.size())));
        this.slots.add(1, SlotsConfig.pokemon.get(RANDOM.nextInt(SlotsConfig.pokemon.size())));
        this.slots.add(2, SlotsConfig.pokemon.get(RANDOM.nextInt(SlotsConfig.pokemon.size())));
        this.addIcon(this.pokemonIcon(10, this.slots.get(0)));
        this.addIcon(this.pokemonIcon(11, this.slots.get(1)));
        this.addIcon(this.pokemonIcon(12, this.slots.get(2)));
    }

    private Icon BorderIcon(int slot, DyeColor color, String name) {
        return new Icon(slot, ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).quantity(1).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{Chat.embedColours((String)name)})).add(Keys.DYE_COLOR, color).build());
    }

    private Icon spinIcon(int slot, Player p) {
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&bCost to spin: &aP" + SlotsConfig.spincost))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&bCurrent balance: &aP" + Money.getBalance((User)p)))}));
        ItemStack item = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:poke_ball").orElse(ItemTypes.CONCRETE)).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, TextStyles.BOLD, "Click here to spin!"})).add(Keys.ITEM_LORE, itemLore).build();
        Icon spinIcon = new Icon(slot, item);
        spinIcon.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                if (Money.canPay((User)this.player, (int)SlotsConfig.spincost)) {
                    if (!this.spinning) {
                        this.spinning = true;
                        Money.withdrawn((User)this.player, (int)SlotsConfig.spincost);
                        this.addIcon(this.spinIcon(14, this.player));
                        this.updateContents(new int[]{14});
                        Task.builder().execute(new SlotTimerTask()).intervalTicks(SlotsConfig.spinSpeed).name("Slot Task - " + this.player.getName()).submit(Casino.getInstance());
                    }
                } else {
                    Chat.sendMessage((CommandSource)this.player, (Text)Chat.embedColours((String)SlotsConfig.getMessages("Messages.not-enough-money")));
                }
            }
        }).delayTicks(1L).submit((Object)Casino.getInstance()));
        return spinIcon;
    }

    private Icon infoIcon(int slot) {
        ArrayList itemLore = new ArrayList();
        SlotsConfig.getAllRewards().forEach(r -> itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)SlotsConfig.getMessages("Messages.rewards-format").replace("{pokemon}", r.getPokemon()).replace("{count}", String.valueOf(r.getCount())).replace("{reward}", r.getDisplay_name()))})));
        return new Icon(slot, ItemStack.builder().itemType(ItemTypes.CONCRETE).quantity(1).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{Chat.embedColours((String)"&6Rewards")})).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).add(Keys.ITEM_LORE, itemLore).build());
    }

    private Icon pokemonIcon(int slot, String pokemon) {
        ItemStack item = ItemStackUtil.fromNative(ItemPixelmonSprite.getPhoto(Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(pokemon))));
        item.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, pokemon));
        return new Icon(slot, item);
    }

    private class SlotTimerTask implements Consumer<Task> {
        private int seconds = SlotsConfig.spinTimer;

        private SlotTimerTask() {
        }

        @Override
        public void accept(Task task) {
            --this.seconds;
            SlotsUI.this.setupPokemonIcons();
            SlotsUI.this.updateContents(10, 11, 12);
            if (this.seconds < 1) {
                Utils.processSlotsWinner(SlotsUI.this.slots, SlotsUI.this.player);
                SlotsUI.this.addIcon(SlotsUI.this.spinIcon(14, SlotsUI.this.player));
                SlotsUI.this.updateContents(14);
                SlotsUI.this.spinning = false;
                task.cancel();
            }
        }
    }
}

