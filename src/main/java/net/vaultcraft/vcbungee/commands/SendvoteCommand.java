package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.user.*;
import net.vaultcraft.vcbungee.vote.Votifier;
import net.vaultcraft.vcbungee.vote.model.Vote;

/**
 * Created by Connor on 9/8/14. Designed for the vcbungee project.
 */

public class SendVoteCommand extends Command {

    public SendVoteCommand(String name) {
        super(name);
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungeecord.command.end") || (commandSender instanceof ProxiedPlayer && NetworkUser.fromPlayer((ProxiedPlayer)commandSender).getGroup().hasPermission(Group.DEVELOPER))) {
            if (args.length == 0) {
                Form.at(commandSender, Prefix.ERROR, "Please specify a player to vote for!");
                return;
            }

            String player = args[0];

            Vote vote = new Vote();
            vote.setUsername(player);

            Votifier.getInstance().getListeners().get(0).voteMade(vote);
            Form.at(commandSender, Prefix.SUCCESS, "Test vote sent for player: &e"+player+Prefix.SUCCESS.getChatColor()+"!s");
        } else {
            Form.at(commandSender, Prefix.ERROR, "You do not have permission to use this command!");
        }
    }
}
