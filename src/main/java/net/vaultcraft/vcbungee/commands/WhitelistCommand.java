package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.config.ClassConfig;
import net.vaultcraft.vcbungee.user.GroupUtil;
import net.vaultcraft.vcbungee.user.NetworkUser;
import net.vaultcraft.vcbungee.user.UUIDFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tacticalsk8er on 10/5/2014.
 */
public class WhitelistCommand extends Command {

    @ClassConfig.Config(path = "Whitelist.Toggle")
    public static boolean whitelisted = false;
    @ClassConfig.Config(path = "Whitelist.HardMode")
    public static boolean hardMode = false;
    @ClassConfig.Config(path = "Whitelist.List")
    public static List<String> whitelist = new ArrayList<>();

    public WhitelistCommand(String name) {
        super(name);
        ClassConfig.loadConfig(this.getClass(), VCBungee.getInstance().getConfig());
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!commandSender.getGroups().contains("admin")) {
            return;
        }

        if(args.length == 0) {
            commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "Commands: add, remove, on , off, hard, and soft."));
            return;
        }

        switch (args[0]) {
            case "add":
                executeAdd(commandSender, args);
                break;
            case "remove":
                executeRemove(commandSender, args);
                break;
            case "on":
                executeOn(commandSender);
                break;
            case "off":
                executeOff(commandSender);
                break;
            case "hard":
                executeHard(commandSender);
                break;
            case "soft":
                executeSoft(commandSender);
                break;
            default:
                commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "Commands: add, remove, on , off, hard, and soft."));
                break;
        }
    }

    private void executeAdd(CommandSender commandSender, String[] args) {
        if(args.length == 1) {
            commandSender.sendMessage(new TextComponent(ChatColor.RED + "Error: " + ChatColor.WHITE + "You need to specify a user."));
            return;
        }

        String uuid;
        try {
            uuid = UUIDFetcher.getUUIDOf(args[1]).toString();
        } catch (Exception e) {
            commandSender.sendMessage(new TextComponent(ChatColor.RED + "Error: " + ChatColor.WHITE + "Something happened when trying to get " + args[1] + " uuid."));
            e.printStackTrace();
            return;
        }

        whitelist.add(uuid);
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Success: " + ChatColor.WHITE + "You added " + args[1] + " to the whitelist."));
    }

    private void executeRemove(CommandSender commandSender, String[]  args) {
        if(args.length == 1) {
            commandSender.sendMessage(new TextComponent(ChatColor.RED + "Error: " + ChatColor.WHITE + "You need to specify a user."));
            return;
        }

        String uuid;
        try {
            uuid = UUIDFetcher.getUUIDOf(args[1]).toString();
        } catch (Exception e) {
            commandSender.sendMessage(new TextComponent(ChatColor.RED + "Error: " + ChatColor.WHITE + "Something happened when trying to get " + args[1] + " uuid."));
            e.printStackTrace();
            return;
        }

        if(!whitelist.contains(uuid)) {
            commandSender.sendMessage(new TextComponent(ChatColor.RED + "Error: " + ChatColor.WHITE + args[1] + " is not on the whitelist."));
            return;
        }

        whitelist.remove(uuid);
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Success: " + ChatColor.WHITE + "You removed " + args[1] + " from the whitelist."));
    }

    private void executeOn(CommandSender commandSender) {
        whitelisted = true;
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Success: " + ChatColor.WHITE + "The whitelist is now on."));
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            NetworkUser user = NetworkUser.fromPlayer(player);
            if(!GroupUtil.hasPermission(user.getGroups(), GroupUtil.Group.ADMIN) && WhitelistCommand.isHardMode() && !WhitelistCommand.getWhitelist().contains(user.getUuid())) {
                player.disconnect(new TextComponent("You are not whitelisted!"));
                return;
            } else if(!GroupUtil.hasPermission(user.getGroups(), GroupUtil.Group.HELPER) && !WhitelistCommand.getWhitelist().contains(user.getUuid())) {
                player.disconnect(new TextComponent("You are not whitelisted!"));
                return;
            }
        }
    }

    private void executeOff(CommandSender commandSender) {
        whitelisted = false;
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Success: " + ChatColor.WHITE + "The whitelist is now off."));
    }

    private void executeSoft(CommandSender commandSender) {
        hardMode = false;
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Success: " + ChatColor.WHITE + "The whitelist is now in soft mode."));
    }

    private void executeHard(CommandSender commandSender) {
        hardMode = true;
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Success: " + ChatColor.WHITE + "The whitelist is now in hard mode."));
    }

    public static boolean isWhitelisted() {
        return whitelisted;
    }

    public static boolean isHardMode() {
        return hardMode;
    }

    public static List<String> getWhitelist() {
        return whitelist;
    }

    public static void onDisable() {
        ClassConfig.updateConfig(WhitelistCommand.class, VCBungee.getInstance().getConfig());
        VCBungee.getInstance().saveConfig();
    }
}
