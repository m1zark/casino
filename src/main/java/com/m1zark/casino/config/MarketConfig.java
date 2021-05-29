/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.reflect.TypeToken
 *  ninja.leaping.configurate.ConfigurationNode
 *  ninja.leaping.configurate.ConfigurationOptions
 *  ninja.leaping.configurate.commented.CommentedConfigurationNode
 *  ninja.leaping.configurate.hocon.HoconConfigurationLoader
 *  ninja.leaping.configurate.hocon.HoconConfigurationLoader$Builder
 *  ninja.leaping.configurate.loader.ConfigurationLoader
 *  ninja.leaping.configurate.objectmapping.ObjectMappingException
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.casino.config;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.m1zark.casino.Casino;
import com.m1zark.casino.CasinoInfo;
import com.m1zark.casino.utils.Items;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;

public class MarketConfig {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;
    public static String CURRENCY_NAME;
    public static String NPC_NAME;

    public MarketConfig() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(Casino.getInstance().getConfigDir() + "/data/market.conf");
        loader = HoconConfigurationLoader.builder().setPath(configFile).build();
        try {
            if (!Files.exists(Casino.getInstance().getConfigDir())) {
                Files.createDirectory(Casino.getInstance().getConfigDir());
            }
            if (!Files.exists(configFile)) {
                Files.createFile(configFile);
            }
            if (main == null) {
                main = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            }
            CommentedConfigurationNode markets = main.getNode("Market");
            markets.getNode("Rewards").getList(TypeToken.of(String.class), Lists.newArrayList());
            CommentedConfigurationNode data = main.getNode("Data");
            data.getNode("npcs").getList(TypeToken.of(String.class), Lists.newArrayList());
            CommentedConfigurationNode messages = main.getNode("Messages");
            messages.getNode("market", "purchase-item").getString("&3&lCasino &e\u00c2\u00bb&r &7You successfully purchased a {item} &7for &d{cost} &7{currency_name}!");
            messages.getNode("market", "not-enough-money").getString("&3&lCasino &e\u00c2\u00bb&r &7You do not have enough {currency_name} to purchase {item}!");
            messages.getNode("market", "inventory-full").getString("&3&lCasino &e\u00c2\u00bb&r &7Unable to purchase {item} due to a full inventory. Please clear some space and try again.");
            CommentedConfigurationNode settings = main.getNode("Settings");
            settings.getNode("currency-name").getString("MirageToken");
            settings.getNode("npc-name").getString("Casino Market");
            loader.save(main);
        }
        catch (IOException | ObjectMappingException e) {
            Casino.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(CasinoInfo.ERROR_PREFIX, "There was an issue loading the config...")));
            e.printStackTrace();
            return;
        }
        this.loadRules();
    }

    private static void saveConfig() {
        try {
            loader.save(main);
        }
        catch (IOException var1) {
            var1.printStackTrace();
        }
    }

    public void reload() {
        try {
            main = loader.load();
            this.loadRules();
        }
        catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    private void loadRules() {
        NPC_NAME = main.getNode("Settings", "npc-name").getString();
        CURRENCY_NAME = main.getNode("Settings", "currency-name").getString();
    }

    public static String getMessages(String path) {
        return main.getNode((Object[])path.split("\\.")).getString();
    }

    public static List<Items> getItemList() {
        ArrayList<Items> items = new ArrayList<Items>();
        for (int i = 0; i < main.getNode("Market", "Rewards").getChildrenList().size(); ++i) {
            CommentedConfigurationNode item = main.getNode("Market", "Rewards").getChildrenList().get(i);
            String type = item.getNode("type").getString();
            String id = item.getNode("id").getString();
            String command = item.getNode("command").isVirtual() ? null : item.getNode("command").getString();
            String name = item.getNode("display-name").getString();
            Integer cost = item.getNode("cost").getInt();
            Integer count = item.getNode("data", "count").isVirtual() ? 1 : item.getNode("data", "count").getInt();
            Integer meta = item.getNode("data", "meta").isVirtual() ? null : item.getNode("data", "meta").getInt();
            boolean unbreakable = !item.getNode("data", "unbreakable").isVirtual() && item.getNode("data", "unbreakable").getBoolean();
            String sprite = item.getNode("data", "sprite-data").isVirtual() ? null : item.getNode("data", "sprite-data").getString();
            List lore = Lists.newArrayList();
            if (!item.getNode("data", "lore").isVirtual()) {
                try {
                    lore = item.getNode("data", "lore").getList(TypeToken.of(String.class));
                }
                catch (ObjectMappingException e) {
                    e.printStackTrace();
                }
            }
            LinkedHashMap nbt = new LinkedHashMap();
            if (!item.getNode("data", "nbt").isVirtual() && item.getNode("data", "nbt").getValue() instanceof LinkedHashMap) {
                nbt = (LinkedHashMap)item.getNode("data", "nbt").getValue();
            }
            items.add(new Items(i, type, id, command, name, meta, nbt, unbreakable, lore, sprite, cost, count));
        }
        items.sort(Comparator.comparing(Items::getCost));
        return items;
    }

    public static boolean addNPCs(UUID uuid) {
        try {
            ArrayList<String> temp = new ArrayList<String>(Lists.newArrayList(MarketConfig.getNPCs()));
            temp.add(uuid.toString());
            main.getNode("Data", "npcs").setValue(temp);
            MarketConfig.saveConfig();
            return true;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeNPCs(UUID uuid) {
        try {
            ArrayList temp = new ArrayList(Lists.newArrayList(MarketConfig.getNPCs()));
            temp.remove(uuid.toString());
            main.getNode("Data", "npcs").setValue(temp);
            MarketConfig.saveConfig();
            return true;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getNPCs() {
        try {
            return main.getNode("Data", "npcs").getList(TypeToken.of(String.class));
        }
        catch (NullPointerException | ObjectMappingException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }
}

