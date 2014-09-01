package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vaultcraft.vcbungee.user.Group;
import net.vaultcraft.vcbungee.user.NetworkUser;

/**
 * Created by Connor on 8/30/14. Designed for the vcbungee project.
 */

public class RebootCommand extends Command {

    public RebootCommand(String name) {
        super(name);
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer) || NetworkUser.fromPlayer((ProxiedPlayer)commandSender).getGroup().hasPermission(Group.OWNER)) {
            int time = 60;

            if (args.length != 0) {
                try { time = Integer.parseInt(args[0]); } catch (Exception ex) {}
            }


        }
    }

    private static int[] warn = {
            1,
            2,
            3,
            4,
            5,
            10,
            20,
            30,
            60,
            120
    };

    private static String fromSeconds(int seconds) {
        if (seconds >= 60) {
            return ((int)(seconds/60))+" minutes, "+(seconds%60)+" seconds";
        }
        return seconds+" seconds";
    }
}
