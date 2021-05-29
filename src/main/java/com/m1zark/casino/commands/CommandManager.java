/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandCallable
 *  org.spongepowered.api.command.args.CommandElement
 *  org.spongepowered.api.command.args.GenericArguments
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.command.spec.CommandSpec
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.casino.commands;

import com.m1zark.casino.Casino;
import com.m1zark.casino.CasinoInfo;
import com.m1zark.casino.commands.Game;
import com.m1zark.casino.commands.Reload;
import java.util.HashMap;
import java.util.Map;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {
    private CommandSpec reload = CommandSpec.builder().permission("casino.admin.reload").executor(new Reload()).build();
    private CommandSpec sign = CommandSpec.builder().permission("casino.admin.signs").arguments(GenericArguments.choices(Text.of("type"), new HashMap<String, String>(){
        {
            this.put("slots", "slots");
            this.put("voltorbflip", "voltorbflip");
        }
    })).executor(new Game.sign()).build();

    private CommandSpec lottery = CommandSpec.builder().permission("casino.admin.lottery").arguments(GenericArguments.choices(Text.of("type"), new HashMap<String, String>(){
        {
            this.put("set", "set");
            this.put("delete", "delete");
        }
    })).executor(new Game.lottery()).build();

    private CommandSpec market = CommandSpec.builder().permission("casino.admin.market").arguments(GenericArguments.choices(Text.of("type"), new HashMap<String, String>(){
        {
            this.put("set", "set");
            this.put("delete", "delete");
        }
    })).executor(new Game.market()).build();

    private CommandSpec balance = CommandSpec.builder().permission("casino.admin.balance").arguments(GenericArguments.player(Text.of("player")), GenericArguments.integer(Text.of("amount"))).executor(new Game.balance()).build();

    private CommandSpec games = CommandSpec.builder()
            .permission("casino.player.games")
            .arguments(GenericArguments.choices(Text.of("type"), new HashMap<String, String>() {
        {
            this.put("slots", "slots");
            this.put("voltorbflip", "voltorbflip");
            this.put("lottery", "lottery");
        }
    })).executor(new Game()).build();

    private CommandSpec casino = CommandSpec.builder()
            .permission("casino.admin")
            .child(this.reload, "reload")
            .child(this.sign, "sign")
            .child(this.lottery, "lotonpc")
            .child(this.games, "games")
            .child(this.market, "marketnpc")
            .child(this.balance, "give")
            .build();

    public void registerCommands(Casino plugin) {
        Sponge.getCommandManager().register(plugin, this.casino, "casino");
        Casino.getInstance().getConsole().ifPresent(console -> console.sendMessages(Text.of(CasinoInfo.PREFIX, "Registering commands...")));
    }
}

