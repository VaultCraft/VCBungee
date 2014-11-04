package net.vaultcraft.vcbungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.user.Form;
import net.vaultcraft.vcbungee.user.Prefix;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tacticalsk8er on 11/3/2014.
 */
public class IPBanCommand extends Command implements Listener {

    private static Configuration configuration;
    private static List<String> ipBans = new ArrayList<>();

    public IPBanCommand(String name) {
        super(name);
        File configurationFile = new File(VCBungee.getInstance().getDataFolder(), "ipbans.yml");
        if (!configurationFile.exists()) {
            try {
                configurationFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ipBans = configuration.getStringList("IPBans");
    }


    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(args.length == 0) {
            Form.at(commandSender, Prefix.ERROR, "Format: /ipban <player>");
            return;
        }

        String s = args[0];
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(s);
        if(player != null) {
            String ip = getIp(player.getAddress().getAddress()).replace(".", "_");
            ipBans.add(ip);
            player.disconnect(new TextComponent("You have been banned!"));
        }
    }


    @EventHandler
    public void onJoin(PreLoginEvent event) {
        String ip = getIp(event.getConnection().getAddress().getAddress()).replace(".", "_");
        if(ipBans.contains(ip))
            event.getConnection().disconnect(new TextComponent("You are IP banned!"));
    }

    public static void disbale() {
        configuration.set("IPBans", ipBans);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(VCBungee.getInstance().getDataFolder(), "ipbans.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getIp(InetAddress address) {
        byte[] arr = address.getAddress();
        String build = "";
        for (byte b : arr) {
            build += b + ".";
        }

        return build.hashCode() + "";
    }
}
