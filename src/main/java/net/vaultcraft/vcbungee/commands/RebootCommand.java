package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.user.Group;
import net.vaultcraft.vcbungee.user.NetworkUser;

import java.util.concurrent.TimeUnit;

/**
 * Created by Connor on 8/30/14. Designed for the vcbungee project.
 */

public class RebootCommand extends Command {

    public RebootCommand(String name) {
        super(name);
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer) || NetworkUser.fromPlayer((ProxiedPlayer)commandSender).getGroup().hasPermission(Group.MANAGER)) {
            int time = 60;

            if (args.length != 0) {
                try { time = Integer.parseInt(args[0]); } catch (Exception ex) {}
            }

            final int $time = time;

            Runnable killNetwork = new Runnable() {
                private int timeLeft = $time;

                public void run() {
                    for (int warn : RebootCommand.this.warn) {
                        if (warn == timeLeft)
                            ProxyServer.getInstance().broadcast(ChatColor.translateAlternateColorCodes('&', "&5&lV&7&lC&f: &7Rebooting network in &e"+fromSeconds(timeLeft)));
                    }

                    if (timeLeft <= 0)
                        ProxyServer.getInstance().stop();

                    timeLeft--;
                }
            };
            ProxyServer.getInstance().getScheduler().schedule(VCBungee.getInstance(), killNetwork, 1l, 1l, TimeUnit.SECONDS);
        } else {
            commandSender.sendMessage();
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
