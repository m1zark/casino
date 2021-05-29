/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.casino;

import com.m1zark.casino.Casino;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CasinoInfo {
    public static final String ID = "casino";
    public static final String NAME = "Casino";
    public static final String VERSION = "1.1.7-S7.2";
    public static final String DESCRIPTION = "Casino plugin for pixelmon.";
    public static final Text PREFIX = Text.of((Object[])new Object[]{TextColors.AQUA, "Casino ", TextColors.GRAY, "\u00bb ", TextColors.DARK_AQUA});
    public static final Text ERROR_PREFIX = Text.of((Object[])new Object[]{TextColors.RED, "Casino ", TextColors.GRAY, "(", TextColors.RED, "Error", TextColors.GRAY, ") ", TextColors.DARK_RED});
    public static final Text DEBUG_PREFIX = Text.of((Object[])new Object[]{TextColors.AQUA, "Casino ", TextColors.GRAY, "(", TextColors.RED, "Debug", TextColors.GRAY, ") ", TextColors.DARK_AQUA});

    static void startup() {
        Casino.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{TextColors.AQUA, NAME, " v.", TextColors.GREEN, VERSION}), Text.of((Object[])new Object[]{TextColors.GREEN, "Author: ", TextColors.AQUA, "m1zark"}), Text.EMPTY}));
    }

    static boolean dependencyCheck() {
        boolean valid = true;
        for (Dependencies dependency : Dependencies.values()) {
            if (Sponge.getPluginManager().isLoaded(dependency.getDependency())) continue;
            Casino.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{ERROR_PREFIX, Text.of((Object[])new Object[]{TextColors.DARK_RED, "==== Missing Dependency ===="})})}));
            Casino.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{ERROR_PREFIX, Text.of((Object[])new Object[]{TextColors.DARK_RED, "  Dependency: ", TextColors.RED, dependency.name()})})}));
            Casino.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{ERROR_PREFIX, Text.of((Object[])new Object[]{TextColors.DARK_RED, "  Version: ", TextColors.RED, dependency.getVersion()})})}));
            valid = false;
        }
        return valid;
    }

    public static enum Dependencies {
        Pixelmon("pixelmon", "8.x.x+");

        private String dependency;
        private String version;

        private Dependencies(String dependency, String version) {
            this.dependency = dependency;
            this.version = version;
        }

        public String getDependency() {
            return this.dependency;
        }

        public String getVersion() {
            return this.version;
        }
    }
}

