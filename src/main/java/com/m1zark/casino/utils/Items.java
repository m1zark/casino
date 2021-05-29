/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.data.DataContainer
 *  org.spongepowered.api.data.DataQuery
 *  org.spongepowered.api.data.DataView
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.item.ItemType
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.serializer.TextSerializers
 */
package com.m1zark.casino.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class Items {
    private final Integer index;
    private final String type;
    private final String id;
    private final String command;
    private final String name;
    private final Integer meta;
    private final Map nbt;
    private final boolean unbreakable;
    private List<String> lore;
    private final String sprite_data;
    private final Integer cost;
    private final Integer count;

    public ItemStack parseItem() {
        Optional itemType = Sponge.getGame().getRegistry().getType(ItemType.class, this.id);
        if (itemType.isPresent()) {
            ItemStack stack = ItemStack.of((ItemType)itemType.get(), 1);
            DataContainer container = stack.toContainer();
            if (this.meta != null) {
                container.set(DataQuery.of("UnsafeDamage"), this.meta);
            }
            if (this.unbreakable) {
                container.set(DataQuery.of("UnsafeData", "Unbreakable"), 1);
                container.set(DataQuery.of("UnsafeData", "HideFlags"), 63);
            }
            if (!this.lore.isEmpty()) {
                ArrayList<Text> realLore = new ArrayList<Text>();
                for (String line : this.lore) {
                    realLore.add(TextSerializers.FORMATTING_CODE.deserialize(line));
                }
                stack.offer(Keys.ITEM_LORE, realLore);
            }
            if (!this.nbt.isEmpty()) {
                if (container.get(DataQuery.of("UnsafeData")).isPresent()) {
                    Map real = container.getMap(DataQuery.of("UnsafeData")).get();
                    this.nbt.putAll(real);
                }
                container.set(DataQuery.of("UnsafeData"), this.nbt);
            }
            if (this.sprite_data != null) {
                container.set(DataQuery.of("UnsafeData", "SpriteName"), this.sprite_data);
            }
            stack = ItemStack.builder().fromContainer(container).build();
            return stack;
        }
        return null;
    }

    public String cmdParser(Player player) {
        StringBuilder cmd = new StringBuilder();
        for (String part : this.command.split(" ")) {
            if (part.contains("{p}")) {
                part = part.replace(part, player.getName());
            }
            cmd.append(" ").append(part);
        }
        return cmd.substring(1);
    }

    public Integer getIndex() {
        return this.index;
    }

    public String getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getCommand() {
        return this.command;
    }

    public String getName() {
        return this.name;
    }

    public Integer getMeta() {
        return this.meta;
    }

    public Map getNbt() {
        return this.nbt;
    }

    public boolean isUnbreakable() {
        return this.unbreakable;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public String getSprite_data() {
        return this.sprite_data;
    }

    public Integer getCost() {
        return this.cost;
    }

    public Integer getCount() {
        return this.count;
    }

    public Items(Integer index, String type, String id, String command, String name, Integer meta, Map nbt, boolean unbreakable, List<String> lore, String sprite_data, Integer cost, Integer count) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.command = command;
        this.name = name;
        this.meta = meta;
        this.nbt = nbt;
        this.unbreakable = unbreakable;
        this.lore = lore;
        this.sprite_data = sprite_data;
        this.cost = cost;
        this.count = count;
    }
}

