/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 */
package com.m1zark.casino.commands;

import com.m1zark.casino.Casino;
import com.m1zark.m1utilities.api.Chat;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Reload
implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Casino.getInstance().getSlotsConfig().reload();
        Casino.getInstance().getVoltorbFlipConfig().reload();
        Casino.getInstance().getLotteryConfig().reload();
        Casino.getInstance().getCooldowns().reload();
        Casino.getInstance().getMarket().reload();
        Chat.sendMessage((CommandSource)src, (String)"&7Casino configs successfully reloaded.");
        return CommandResult.success();
    }
}

