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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;

public class LotteryConfig {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;
    public static int cooldown;
    public static int cost;
    public static String npc_name;
    public static boolean check_pc;
    public static boolean debug;
    public static int reward_1;
    public static int reward_2;
    public static int reward_3;
    public static int reward_4;
    public static int reward_5;

    public LotteryConfig() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(Casino.getInstance().getConfigDir() + "/lottery-settings.conf", new String[0]);
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
            settings.getNode(new Object[]{"cooldown"}).setComment("This is in seconds (86400 = one day)").getInt(86400);
            settings.getNode(new Object[]{"npc-name"}).getString("Loto Attendant");
            settings.getNode(new Object[]{"check-pc"}).setComment("Checks the players PC as well as their party.").getBoolean(false);
            settings.getNode(new Object[]{"cost"}).setComment("Set to 0 for no cost... obviously").getInt(150);
            settings.getNode(new Object[]{"console-debug"}).getBoolean(false);
            CommentedConfigurationNode messages = main.getNode(new Object[]{"Messages"});
            messages.getNode(new Object[]{"npc", "dialogue", "welcome-message"}).getList(TypeToken.of(String.class), (List)Lists.newArrayList((Object[])new String[]{"This is the Loto-ID Center!", "If your drawn number matches the ID No. of any of your Pok\u00e9mon, you could win fabulous prizes!"}));
            messages.getNode(new Object[]{"npc", "dialogue", "no-funds"}).getString("Oh it looks like you don't have enough funds to buy a ticket. It's P{cost} to draw a number.");
            messages.getNode(new Object[]{"npc", "dialogue", "prompt", "welcome-question"}).getString("Try your luck today! Would you like to draw a Loto Ticket for P{cost}?");
            messages.getNode(new Object[]{"npc", "dialogue", "prompt", "yes"}).getString("Your Loto Ticket number is {number}! Let's see if it matches the ID No. of any of your Pok\u00e9mon!");
            messages.getNode(new Object[]{"npc", "dialogue", "prompt", "no"}).getString("Please do visit again!");
            messages.getNode(new Object[]{"npc", "dialogue", "lose"}).getString("I'm sorry. It looks like you didn't get a match this time...");
            messages.getNode(new Object[]{"npc", "dialogue", "win", "in-party"}).getString("Congratulations! Spectacularly, the ID number of your party's Pok\u00e9mon matches your Loto Ticket's number!");
            messages.getNode(new Object[]{"npc", "dialogue", "win", "in-pc"}).getString("Congratulations! The ID No. of your team's Pok\u00e9mon matches your Loto Ticket number!");
            messages.getNode(new Object[]{"npc", "dialogue", "win", "match", "1-digit"}).getString("OK. One digit matched. Your luck is OK, I guess! You've won the fourth prize, {prize}!");
            messages.getNode(new Object[]{"npc", "dialogue", "win", "match", "2-digits"}).getString("Two digits matched! You have good luck! You've won the third prize, {prize}!");
            messages.getNode(new Object[]{"npc", "dialogue", "win", "match", "3-digits"}).getString("Oh! Three digits matched! Your luck is really special! You've won the second prize, {prize}!");
            messages.getNode(new Object[]{"npc", "dialogue", "win", "match", "4-digits"}).getString("Great! Four digits matched! You have excellent luck! You've won the first prize, {prize}!");
            messages.getNode(new Object[]{"npc", "dialogue", "win", "match", "5-digits"}).getString("Oh my goodness, five digits matched! You have incredible luck! You've won the jackpot prize, {prize}!");
            messages.getNode(new Object[]{"npc", "dialogue", "already-drawn"}).getString("You can draw another Loto Ticket in {time}. Please come back then!");
            CommentedConfigurationNode prizes = main.getNode(new Object[]{"Prizes"});
            prizes.getNode(new Object[]{"1-digit"}).setComment("Your pretty much guaranteed this reward every time, especially if you enable pc checking.").getInt(1);
            prizes.getNode(new Object[]{"2-digits"}).getInt(2);
            prizes.getNode(new Object[]{"3-digits"}).getInt(3);
            prizes.getNode(new Object[]{"4-digits"}).getInt(4);
            prizes.getNode(new Object[]{"5-digits"}).getInt(5);
            CommentedConfigurationNode data = main.getNode(new Object[]{"Data"}).setComment("Don't touch this!");
            data.getNode(new Object[]{"npcs"}).getList(TypeToken.of(String.class), (List)Lists.newArrayList());
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
        cooldown = main.getNode(new Object[]{"Settings", "cooldown"}).getInt();
        npc_name = main.getNode(new Object[]{"Settings", "npc-name"}).getString();
        check_pc = main.getNode(new Object[]{"Settings", "check-pc"}).getBoolean();
        cost = main.getNode(new Object[]{"Settings", "cost"}).getInt();
        debug = main.getNode(new Object[]{"Settings", "console-debug"}).getBoolean();
        reward_1 = main.getNode(new Object[]{"Prizes", "1-digit"}).getInt();
        reward_2 = main.getNode(new Object[]{"Prizes", "2-digits"}).getInt();
        reward_3 = main.getNode(new Object[]{"Prizes", "3-digits"}).getInt();
        reward_4 = main.getNode(new Object[]{"Prizes", "4-digits"}).getInt();
        reward_5 = main.getNode(new Object[]{"Prizes", "5-digits"}).getInt();
    }

    public static String getMessages(String path) {
        return main.getNode((Object[])path.split("\\.")).getString();
    }

    public static List<String> getMessageLists(String path) {
        try {
            return main.getNode((Object[])path.split("\\.")).getList(TypeToken.of(String.class));
        }
        catch (ObjectMappingException e) {
            return Lists.newArrayList();
        }
    }

    public static boolean addNPCs(UUID uuid) {
        try {
            ArrayList<String> temp = new ArrayList<String>(Lists.newArrayList(LotteryConfig.getNPCs()));
            temp.add(uuid.toString());
            main.getNode(new Object[]{"Data", "npcs"}).setValue(temp);
            LotteryConfig.saveConfig();
            return true;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeNPCs(UUID uuid) {
        try {
            ArrayList temp = new ArrayList(Lists.newArrayList(LotteryConfig.getNPCs()));
            temp.remove(uuid.toString());
            main.getNode(new Object[]{"Data", "npcs"}).setValue(temp);
            LotteryConfig.saveConfig();
            return true;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getNPCs() {
        try {
            return main.getNode(new Object[]{"Data", "npcs"}).getList(TypeToken.of(String.class));
        }
        catch (NullPointerException | ObjectMappingException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }
}

