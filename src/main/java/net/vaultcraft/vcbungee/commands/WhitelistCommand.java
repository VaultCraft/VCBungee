package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vaultcraft.vcbungee.user.GroupUtil;
import net.vaultcraft.vcbungee.user.NetworkUser;
import net.vaultcraft.vcbungee.user.UUIDFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tacticalsk8er on 10/5/2014.
 */
public class WhitelistCommand extends Command {

    private static boolean whitelisted = false;
    private static List<String> whitelist = new ArrayList<>();

    public WhitelistCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!commandSender.getGroups().contains("admin")) {
            return;
        }

        if(args.length == 0) {
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
            if(!GroupUtil.hasPermission(NetworkUser.fromPlayer(player).getGroups(),GroupUtil.Group.ADMIN)) {
                player.disconnect(new TextComponent("You are not whitelisted!"));
            }
        }
    }

    private void executeOff(CommandSender commandSender) {
        whitelisted = false;
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Success: " + ChatColor.WHITE + "The whitelist is now off."));
    }

    public static boolean isWhitelisted() {
        return whitelisted;
    }
}
