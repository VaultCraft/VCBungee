package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vaultcraft.vcbungee.listeners.BungeeListener;
import net.vaultcraft.vcbungee.user.GroupUtil;
import net.vaultcraft.vcbungee.user.NetworkUser;

/**
 * @author Connor Hollasch
 * @since 10/18/14
 */
public class SetReleaseTime extends Command {

    public SetReleaseTime(String name) {
        super(name);
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer))
            return;

        ProxiedPlayer p = (ProxiedPlayer)commandSender;
        if (GroupUtil.hasPermission(NetworkUser.fromPlayer(p).getGroups(), GroupUtil.Group.MANAGER)) {
            if (args.length == 0) {
                p.sendMessage(ChatColor.RED+"Needs arguments for time!");
                return;
            }

            Long l = Long.valueOf(args[0]);

            BungeeListener.release = System.currentTimeMillis() + (l * 1000);
        }
    }
}
