/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.reflect.TypeToken
 *  com.pixelmonmod.pixelmon.enums.EnumSpecies
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
import com.m1zark.casino.config.MarketConfig;
import com.m1zark.casino.utils.Rewards;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;

public class SlotsConfig {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;
    public static List<String> pokemon;
    public static int spincost;
    public static int spinTimer;
    public static int spinSpeed;

    public SlotsConfig() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(Casino.getInstance().getConfigDir() + "/slots-settings.conf", new String[0]);
        loader = ((HoconConfigurationLoader.Builder)HoconConfigurationLoader.builder().setPath(configFile)).build();
        try {
            if (!Files.exists(Casino.getInstance().getConfigDir(), new LinkOption[0])) {
                Files.createDirectory(Casino.getInstance().getConfigDir(), new FileAttribute[0]);
            }
            if (!Files.exists(configFile, new LinkOption[0])) {
                Files.createFile(configFile, new FileAttribute[0]);
            }
            if (main == null) {
                main = (CommentedConfigurationNode)loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            }
            CommentedConfigurationNode settings = main.getNode(new Object[]{"Settings"});
            settings.getNode(new Object[]{"pokemon-spinner"}).setComment("List of Pokemon to show in the slots spinner. This plus the rewards determines the odds of winning.").getList(TypeToken.of(String.class), (List)Lists.newArrayList((Object[])new String[]{"Pikachu", "Abra", "Mew", "Chansey", "Staryu", "Porygon"}));
            settings.getNode(new Object[]{"spin-cost"}).getInt(200);
            settings.getNode(new Object[]{"spin-timer"}).getInt(30);
            settings.getNode(new Object[]{"spin-speed"}).getInt(4);
            settings.getNode(new Object[]{"rewards"}).getList(TypeToken.of(String.class), (List)Lists.newArrayList());
            CommentedConfigurationNode messages = main.getNode(new Object[]{"Messages"});
            messages.getNode(new Object[]{"win"}).getString("&6Casino &7\u00bb &bYou matched {matches} {pokemon}! You won {reward}!");
            messages.getNode(new Object[]{"lose"}).getString("&6Casino &7\u00bb &bSorry you didn't win. Better luck next time.");
            messages.getNode(new Object[]{"not-enough-money"}).getString("&6Casino &7\u00bb &bYou currently don't have enough money to play.");
            messages.getNode(new Object[]{"rewards-format"}).getString("&b{pokemon} x{count}&7: &a{reward}");
            CommentedConfigurationNode data = main.getNode(new Object[]{"Data"}).setComment("Do not touch this!");
            data.getNode(new Object[]{"signs"}).getList(TypeToken.of(String.class), (List)Lists.newArrayList());
            loader.save((ConfigurationNode)main);
        }
        catch (IOException | ObjectMappingException e) {
            Casino.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{CasinoInfo.ERROR_PREFIX, "There was an issue loading the config..."})}));
            e.printStackTrace();
            return;
        }
        this.loadRules();
    }

    private static void saveConfig() {
        try {
            loader.save((ConfigurationNode)main);
        }
        catch (IOException var1) {
            var1.printStackTrace();
        }
    }

    public void reload() {
        try {
            main = (CommentedConfigurationNode)loader.load();
            this.loadRules();
        }
        catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    private void loadRules() {
        try {
            pokemon = main.getNode(new Object[]{"Settings", "pokemon-spinner"}).getList(TypeToken.of(String.class)).stream().filter(EnumSpecies::hasPokemonAnyCase).collect(Collectors.toList());
            spincost = main.getNode(new Object[]{"Settings", "spin-cost"}).getInt();
            spinTimer = main.getNode(new Object[]{"Settings", "spin-timer"}).getInt();
            spinSpeed = main.getNode(new Object[]{"Settings", "spin-speed"}).getInt();
        }
        catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static String getMessages(String path) {
        return main.getNode((Object[])path.split("\\.")).getString();
    }

    public static boolean hasRewards(String pokemon, int count) {
        List<Rewards> rewards = SlotsConfig.getAllRewards();
        return rewards.size() != 0 && rewards.stream().anyMatch(r -> r.getPokemon().equalsIgnoreCase(pokemon) && r.getCount() == count);
    }

    public static int getRewards(String pokemon, int count) {
        List<Rewards> rewards = SlotsConfig.getAllRewards();
        Optional<Rewards> reward = rewards.stream().filter(r -> r.getPokemon().equalsIgnoreCase(pokemon) && r.getCount() == count).findAny();
        return reward.isPresent() ? reward.get().getReward() : 0;
    }

    public static List<Rewards> getAllRewards() {
        ArrayList<Rewards> rewards = new ArrayList<Rewards>();
        for (int i = 0; i < main.getNode(new Object[]{"Settings", "rewards"}).getChildrenList().size(); ++i) {
            CommentedConfigurationNode reward = (CommentedConfigurationNode)main.getNode(new Object[]{"Settings", "rewards"}).getChildrenList().get(i);
            rewards.add(new Rewards(reward.getNode(new Object[]{"count"}).getInt(), 0, reward.getNode(new Object[]{"reward"}).getInt(), reward.getNode(new Object[]{"pokemon"}).getString(), "", reward.getNode(new Object[]{"reward"}).getInt() + " " + MarketConfig.CURRENCY_NAME));
        }
        return rewards;
    }

    public static boolean addSignLocation(String location) {
        try {
            ArrayList<String> temp = new ArrayList<String>(Lists.newArrayList(SlotsConfig.getSignLocations()));
            temp.add(location);
            main.getNode(new Object[]{"Data", "signs"}).setValue(temp);
            SlotsConfig.saveConfig();
            return true;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeSignLocation(String location) {
        try {
            ArrayList temp = new ArrayList(Lists.newArrayList(SlotsConfig.getSignLocations()));
            temp.remove(location);
            main.getNode(new Object[]{"Data", "signs"}).setValue(temp);
            SlotsConfig.saveConfig();
            return true;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getSignLocations() {
        try {
            return main.getNode(new Object[]{"Data", "signs"}).getList(TypeToken.of(String.class));
        }
        catch (NullPointerException | ObjectMappingException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }
}

