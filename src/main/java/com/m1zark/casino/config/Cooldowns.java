/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  ninja.leaping.configurate.ConfigurationNode
 *  ninja.leaping.configurate.ConfigurationOptions
 *  ninja.leaping.configurate.commented.CommentedConfigurationNode
 *  ninja.leaping.configurate.hocon.HoconConfigurationLoader
 *  ninja.leaping.configurate.hocon.HoconConfigurationLoader$Builder
 *  ninja.leaping.configurate.loader.ConfigurationLoader
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.casino.config;

import com.m1zark.casino.Casino;
import com.m1zark.casino.CasinoInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.UUID;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.text.Text;

public class Cooldowns {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;

    public Cooldowns() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(Casino.getInstance().getConfigDir() + "/data/cooldowns.conf", new String[0]);
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
            CommentedConfigurationNode cooldowns = main.getNode(new Object[]{"Cooldowns"});
            loader.save((ConfigurationNode)main);
        }
        catch (IOException e) {
            Casino.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{CasinoInfo.ERROR_PREFIX, "There was an issue loading the config..."})}));
            e.printStackTrace();
            return;
        }
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
        }
        catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public static boolean saveCooldown(String game, UUID uuid, long time) {
        main.getNode(new Object[]{"Cooldowns", game, uuid.toString()}).setValue((Object)time);
        Cooldowns.saveConfig();
        return true;
    }

    public static HashMap<String, Long> getCooldown(String game) {
        HashMap<String, Long> data = new HashMap<String, Long>();
        main.getNode(new Object[]{"Cooldowns", game}).getChildrenMap().forEach((player, time) -> data.put((String)player, time.getLong()));
        return data;
    }
}

