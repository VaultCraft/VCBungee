package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vaultcraft.vcbungee.user.GroupUtil;
import net.vaultcraft.vcbungee.user.NetworkUser;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author Connor Hollasch
 * @since 10/9/14
 */
public class ShoutCommand extends Command {

    private static HashMap<String, Long> cooldown = new HashMap<>();

    public ShoutCommand(String name) {
        super(name);
    }

    public void execute(CommandSender commandSender, String[] args) {
        boolean use = (!(commandSender instanceof ProxiedPlayer));

        if (!use) {
            NetworkUser user = NetworkUser.fromPlayer((ProxiedPlayer)commandSender);
            for (int group : user.getGroups()) {
                if (group >= GroupUtil.Group.WOLF.getPermissionLevel())
                    use = true;
            }
        }

        if (!(use)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lERROR&7: &fYou do not have permission for this command!"));
            return;
        }

        if (cooldown.containsKey(commandSender.getName())) {
            long time = cooldown.get(commandSender.getName());
            if (time <= System.currentTimeMillis())
                cooldown.remove(commandSender.getName());
            else {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lERROR&f: &7You cannot use this feature for another " + time(commandSender) + " second(s)!"));
                return;
            }
        }

        //Collect arguments
        String argMsg = "";
        for (String arg : args) {
            argMsg+=(arg + " ");
        }

        ProxyServer.getInstance().broadcast(ChatColor.translateAlternateColorCodes('&', "&e&l" + commandSender.getName() + " &f- &c&lSHOUT&f: &7" + argMsg));
        cooldown.put(commandSender.getName(), System.currentTimeMillis() + (60000));
    }

    private static final DecimalFormat df = new DecimalFormat("##.#");

    private String time(CommandSender sender) {
        long at = cooldown.get(sender.getName());
        long curr = System.currentTimeMillis();

        long diff = (at - curr);

        double seconds = (double)((double)diff/1000.0);
        return df.format(seconds);
    }
}
