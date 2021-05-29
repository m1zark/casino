/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  org.slf4j.Logger
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.source.ConsoleSource
 *  org.spongepowered.api.config.ConfigDir
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.game.GameReloadEvent
 *  org.spongepowered.api.event.game.state.GameInitializationEvent
 *  org.spongepowered.api.event.game.state.GamePostInitializationEvent
 *  org.spongepowered.api.event.game.state.GameStoppingEvent
 *  org.spongepowered.api.plugin.Plugin
 *  org.spongepowered.api.plugin.PluginContainer
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.casino;

import com.google.inject.Inject;
import com.m1zark.casino.CasinoInfo;
import com.m1zark.casino.commands.CommandManager;
import com.m1zark.casino.config.Cooldowns;
import com.m1zark.casino.config.LotteryConfig;
import com.m1zark.casino.config.MarketConfig;
import com.m1zark.casino.config.SlotsConfig;
import com.m1zark.casino.config.VoltorbFlipConfig;
import com.m1zark.casino.listeners.NPCListener;
import com.m1zark.casino.listeners.SignListener;
import com.m1zark.casino.storage.DataSource;
import com.m1zark.casino.utils.Placeholders;
import com.m1zark.casino.utils.Rewards;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

@Plugin(id="casino", name="Casino", version="1.1.7-S7.2", description="Casino plugin for pixelmon.", authors={"m1zark"})
public class Casino {
    @Inject
    private Logger logger;
    @Inject
    private PluginContainer pluginContainer;
    private static Casino instance;
    private DataSource sql;
    @Inject
    @ConfigDir(sharedRoot=false)
    private Path configDir;
    private SlotsConfig slotsConfig;
    private VoltorbFlipConfig voltorbFlipConfig;
    private LotteryConfig lotteryConfig;
    private Cooldowns cooldowns;
    private MarketConfig market;
    private boolean enabled = true;
    private HashMap<UUID, List<Rewards>> voltorbflip = new HashMap();

    @Listener
    public void onInitialization(GameInitializationEvent e) {
        instance = this;
        CasinoInfo.startup();
        this.enabled = CasinoInfo.dependencyCheck();
        if (this.enabled) {
            this.sql = new DataSource("Casino_PlayerData");
            this.sql.createTables();
            this.slotsConfig = new SlotsConfig();
            this.voltorbFlipConfig = new VoltorbFlipConfig();
            this.lotteryConfig = new LotteryConfig();
            this.cooldowns = new Cooldowns();
            this.market = new MarketConfig();
            Sponge.getEventManager().registerListeners(this, new SignListener());
            Sponge.getEventManager().registerListeners(this, new NPCListener());
            new CommandManager().registerCommands(this);
        }
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent e) {
        if (Sponge.getPluginManager().isLoaded("placeholderapi")) {
            Placeholders.register(this);
        }
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        if (this.enabled) {
            this.slotsConfig = new SlotsConfig();
            this.voltorbFlipConfig = new VoltorbFlipConfig();
            this.lotteryConfig = new LotteryConfig();
            this.cooldowns = new Cooldowns();
            this.market = new MarketConfig();
            this.getConsole().ifPresent(console -> console.sendMessages(Text.of(CasinoInfo.PREFIX, "Configurations have been reloaded")));
        }
    }

    @Listener
    public void onServerStop(GameStoppingEvent e) {
        try {
            this.sql.shutdown();
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static Casino getInstance() {
        return instance;
    }

    public Optional<ConsoleSource> getConsole() {
        return Optional.ofNullable(Sponge.isServerAvailable() ? Sponge.getServer().getConsole() : null);
    }

    public Logger getLogger() {
        return this.logger;
    }

    public PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }

    public DataSource getSql() {
        return this.sql;
    }

    public Path getConfigDir() {
        return this.configDir;
    }

    public SlotsConfig getSlotsConfig() {
        return this.slotsConfig;
    }

    public VoltorbFlipConfig getVoltorbFlipConfig() {
        return this.voltorbFlipConfig;
    }

    public LotteryConfig getLotteryConfig() {
        return this.lotteryConfig;
    }

    public Cooldowns getCooldowns() {
        return this.cooldowns;
    }

    public MarketConfig getMarket() {
        return this.market;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public HashMap<UUID, List<Rewards>> getVoltorbflip() {
        return this.voltorbflip;
    }
}

