/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  com.m1zark.m1utilities.api.Inventories
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.data.type.DyeColor
 *  org.spongepowered.api.data.type.DyeColors
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.item.ItemType
 *  org.spongepowered.api.item.ItemTypes
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 *  org.spongepowered.api.text.serializer.TextSerializers
 */
package com.m1zark.casino.gui;

import com.m1zark.casino.Casino;
import com.m1zark.casino.config.MarketConfig;
import com.m1zark.casino.utils.Items;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.Inventories;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

public class MarketUI
extends InventoryManager {
    private Player player;
    private int page = 1;
    private int maxPage;
    private int balance;

    public MarketUI(Player p) {
        super(p, 6, Text.of(Chat.embedColours("&4&lCasino MarketPlace")));
        this.player = p;
        this.balance = Casino.getInstance().getSql().getBalance(p.getUniqueId());
        int size = MarketConfig.getItemList().size();
        this.maxPage = size % 36 == 0 && size / 36 != 0 ? size / 36 : size / 36 + 1;
        this.setupInventory();
        this.setupItems();
    }

    private void setupInventory() {
        int y = 4;
        for (int x = 0; x < 9; ++x) {
            this.addIcon(MarketUI.BorderIcon(x + 9 * y, DyeColors.GRAY, ""));
        }
        Icon previousPage = MarketUI.pageIcon(48, false);
        previousPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> {
            this.page = this.page > 1 ? --this.page : this.maxPage;
            this.clearIcons(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
            this.setupItems();
            this.updateContents(0, 35);
        }).delayTicks(1L).submit(Casino.getInstance()));
        this.addIcon(previousPage);
        this.addIcon(MarketUI.infoIcon(49, this.player));
        Icon nextPage = MarketUI.pageIcon(50, true);
        nextPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> {
            this.page = this.page < this.maxPage ? ++this.page : 1;
            this.clearIcons(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
            this.setupItems();
            this.updateContents(0, 35);
        }).delayTicks(1L).submit(Casino.getInstance()));
        this.addIcon(nextPage);
    }

    private void setupItems() {
        List<Items> items = MarketConfig.getItemList();
        int index = (this.page - 1) * 36;
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 9 && index < items.size(); ++x) {
                int pos = index++;
                Icon item = MarketUI.itemIcon(x + 9 * y, items.get(pos));
                item.addListener(clickable -> {
                    int id = ((Items)items.get(pos)).getIndex();
                    Optional<Items> itemData = MarketConfig.getItemList().stream().filter(listing -> listing.getIndex() == id).findFirst();
                    itemData.ifPresent(data -> Sponge.getScheduler().createTaskBuilder().execute(() -> {
                        if (this.balance - data.getCost() >= 0) {
                            if (data.getType().equalsIgnoreCase("item")) {
                                if (Inventories.giveItem(this.player, data.parseItem(), data.getCount())) {
                                    Casino.getInstance().getSql().updateBalanceTotal(this.player.getUniqueId(), true, data.getCost());
                                    this.balance = Casino.getInstance().getSql().getBalance(this.player.getUniqueId());
                                    Chat.sendMessage(this.player, MarketConfig.getMessages("Messages.market.purchase-item").replace("{item}", data.getName()).replace("{cost}", String.valueOf(data.getCost())).replace("{currency_name}", MarketConfig.CURRENCY_NAME));
                                } else {
                                    Chat.sendMessage(this.player, MarketConfig.getMessages("Messages.market.inventory-full").replace("{item}", data.getName()));
                                }
                            } else if (data.getType().equalsIgnoreCase("command")) {
                                Casino.getInstance().getSql().updateBalanceTotal(this.player.getUniqueId(), true, data.getCost());
                                this.balance = Casino.getInstance().getSql().getBalance(this.player.getUniqueId());
                                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), data.cmdParser(this.player));
                                Chat.sendMessage(this.player, MarketConfig.getMessages("Messages.market.purchase-item").replace("{item}", data.getName()).replace("{cost}", String.valueOf(data.getCost())).replace("{currency_name}", MarketConfig.CURRENCY_NAME));
                            }
                            this.clearIcons(49);
                            this.addIcon(MarketUI.infoIcon(49, this.player));
                            this.updateContents(49);
                        } else {
                            Chat.sendMessage(this.player, MarketConfig.getMessages("Messages.market.not-enough-money").replace("{item}", data.getName()).replace("{cost}", String.valueOf(data.getCost())).replace("{currency_name}", MarketConfig.CURRENCY_NAME));
                        }
                    }).delayTicks(1L).submit(Casino.getInstance()));
                });
                this.addIcon(item);
            }
        }
    }

    private static Icon BorderIcon(int slot, DyeColor color, String name) {
        return new Icon(slot, ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).quantity(1).add(Keys.DISPLAY_NAME, Text.of(Chat.embedColours(name))).add(Keys.DYE_COLOR, color).build());
    }

    private static Icon itemIcon(int slot, Items item) {
        ItemStack ItemStack2 = item.parseItem();
        ItemStack2.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(item.getName()));
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of(Chat.embedColours("&aPurchase Cost: &d" + item.getCost() + " " + MarketConfig.CURRENCY_NAME)));
        itemLore.add(Text.of(Chat.embedColours("")));
        ItemStack2.get(Keys.ITEM_LORE).ifPresent(lore -> lore.forEach(text -> itemLore.add(Text.of(text))));
        ItemStack2.offer(Keys.ITEM_LORE, itemLore);
        return new Icon(slot, ItemStack2);
    }

    private static Icon pageIcon(int slot, boolean nextOrLast) {
        return new Icon(slot, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, nextOrLast ? "pixelmon:trade_holder_right" : "pixelmon:trade_holder_left").get()).quantity(1).add(Keys.DISPLAY_NAME, (nextOrLast ? Text.of(TextColors.GREEN, "\u2192 ", "Next Page", TextColors.GREEN, " \u2192") : Text.of(TextColors.RED, "\u2190 ", "Previous Page", TextColors.RED, " \u2190"))).build());
    }

    private static Icon infoIcon(int slot, Player p) {
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of(Chat.embedColours("&b" + MarketConfig.CURRENCY_NAME + " Balance: &a" + Casino.getInstance().getSql().getBalance(p.getUniqueId()))));
        return new Icon(slot, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_panel").get()).quantity(1).add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Player Information")).add(Keys.ITEM_LORE, itemLore).build());
    }
}

