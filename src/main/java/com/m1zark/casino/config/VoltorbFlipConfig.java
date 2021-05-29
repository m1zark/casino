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
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.casino.config;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.m1zark.casino.Casino;
import com.m1zark.casino.CasinoInfo;
import com.m1zark.casino.config.MarketConfig;
import com.m1zark.casino.utils.Rewards;
import com.m1zark.casino.utils.Utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class VoltorbFlipConfig {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;
    public static int maxLevel;
    public static int maxScore;
    public static int cost;
    public static int cooldown;
    public static boolean debug;

    public VoltorbFlipConfig() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(Casino.getInstance().getConfigDir() + "/voltorb-settings.conf", new String[0]);
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
            settings.getNode(new Object[]{"console-debug"}).getBoolean(false);
            settings.getNode(new Object[]{"cost-to-play"}).getInt(1000);
            settings.getNode(new Object[]{"max-levels"}).getInt(8);
            settings.getNode(new Object[]{"max-score"}).getInt(10000);
            settings.getNode(new Object[]{"reward-cooldown"}).setComment("This is in seconds (86400 = one day)").getInt(86400);
            CommentedConfigurationNode messages = main.getNode(new Object[]{"Messages"});
            messages.getNode(new Object[]{"win"}).getString("&6Casino &7\u00bb &bYou got a high score of {score}! You won {reward}!");
            messages.getNode(new Object[]{"not-enough-money"}).getString("&6Casino &7\u00bb &bYou currently don't have enough money to play.");
            messages.getNode(new Object[]{"reward-cooldown"}).getString("&6Casino &7\u00bb &bYou already received your prize. You can win another prize in {time}.");
            CommentedConfigurationNode rewards = main.getNode(new Object[]{"Rewards"});
            rewards.getNode(new Object[]{"Scores"}).getList(TypeToken.of(String.class), (List)Lists.newArrayList());
            CommentedConfigurationNode data = main.getNode(new Object[]{"Data"});
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
        cost = main.getNode(new Object[]{"Settings", "cost-to-play"}).getInt();
        maxLevel = main.getNode(new Object[]{"Settings", "max-levels"}).getInt();
        maxScore = main.getNode(new Object[]{"Settings", "max-score"}).getInt();
        cooldown = main.getNode(new Object[]{"Settings", "reward-cooldown"}).getInt();
        debug = main.getNode(new Object[]{"Settings", "console-debug"}).getBoolean();
    }

    public static String getMessages(String path) {
        return main.getNode((Object[])path.split("\\.")).getString();
    }

    public static boolean hasRewards(int score, Player player) {
        List<Rewards> rewards = VoltorbFlipConfig.Rewards();
        rewards.forEach(reward -> {
            if (Casino.getInstance().getVoltorbflip().get(player.getUniqueId()).contains(reward)) {
                rewards.remove(reward);
            }
        });
        return rewards.size() != 0 && rewards.stream().anyMatch(r -> Utils.between(score, r.getCount(), r.getNextCount()));
    }

    public static List<Rewards> Rewards() {
        ArrayList rewards = Lists.newArrayList();
        int scores = main.getNode(new Object[]{"Rewards", "Scores"}).getChildrenList().size();
        for (int i = 0; i < scores; ++i) {
            CommentedConfigurationNode reward = (CommentedConfigurationNode)main.getNode(new Object[]{"Rewards", "Scores"}).getChildrenList().get(i);
            int nextReward = reward.getNode(new Object[]{"score"}).getInt();
            if (i != scores - 1) {
                CommentedConfigurationNode reward2 = (CommentedConfigurationNode)main.getNode(new Object[]{"Rewards", "Scores"}).getChildrenList().get(i + 1);
                nextReward = reward2.getNode(new Object[]{"score"}).getInt();
            }
            rewards.add(new Rewards(reward.getNode(new Object[]{"score"}).getInt(), nextReward, reward.getNode(new Object[]{"reward"}).getInt(), "", "", reward.getNode(new Object[]{"reward"}).getInt() + " " + MarketConfig.CURRENCY_NAME));
        }
        return rewards;
    }

    public static boolean addSignLocation(String location) {
        try {
            ArrayList<String> temp = new ArrayList<String>(Lists.newArrayList(VoltorbFlipConfig.getSignLocations()));
            temp.add(location);
            main.getNode(new Object[]{"Data", "signs"}).setValue(temp);
            VoltorbFlipConfig.saveConfig();
            return true;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeSignLocation(String location) {
        try {
            ArrayList temp = new ArrayList(Lists.newArrayList(VoltorbFlipConfig.getSignLocations()));
            temp.remove(location);
            main.getNode(new Object[]{"Data", "signs"}).setValue(temp);
            VoltorbFlipConfig.saveConfig();
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

