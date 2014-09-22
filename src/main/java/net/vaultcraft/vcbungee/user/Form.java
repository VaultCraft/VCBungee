package net.vaultcraft.vcbungee.user;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Connor on 9/8/14. Designed for the vcbungee project.
 */

public class Form {

    public static void at(CommandSender player, Prefix prefix, String message) {
        String sent = ChatColor.translateAlternateColorCodes('&', prefix.getPrefix()+message+prefix.getSuffix());
        player.sendMessage(sent);
    }

}
