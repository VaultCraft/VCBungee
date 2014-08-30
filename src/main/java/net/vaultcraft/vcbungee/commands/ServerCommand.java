package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vaultcraft.vcbungee.user.NetworkUser;

/**
 * Created by Connor on 8/27/14. Designed for the vcbungee project.
 */

public class ServerCommand extends Command {

    public ServerCommand(String name) {
        super(name);
    }

    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(ChatColor.RED+"You must be a player to use this command!");

            return;
        }

        NetworkUser user = NetworkUser.fromPlayer((ProxiedPlayer)commandSender);
    }
}
