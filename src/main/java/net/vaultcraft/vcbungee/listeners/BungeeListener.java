package net.vaultcraft.vcbungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.user.NetworkUser;
import net.vaultcraft.vcbungee.user.UUIDFetcher;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class BungeeListener implements Listener {

    public BungeeListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(VCBungee.getInstance(), this);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        new NetworkUser(event.getPlayer());

        String name = event.getPlayer().getName();
        String ip = getIp(event.getPlayer().getAddress().getAddress()).replace(".", "_");

        if (VCBungee.getInstance().getConfig().get("user." + ip) != null)
            return;

        VCBungee.getInstance().getConfig().set("user." + ip, name);
        VCBungee.getInstance().saveConfig();
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        if(NetworkUser.fromPlayer(event.getPlayer()) != null)
            NetworkUser.remove(event.getPlayer());
        UUID uuid;
        try {
            uuid = UUIDFetcher.getUUIDOf(event.getPlayer().getName());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static long release;

    @EventHandler
    public void onPlayerPing(ProxyPingEvent event) throws IOException {
        Configuration conf = VCBungee.getInstance().getConfig();
        String playerName = "MHF_Question";

        String ip = getIp(event.getConnection().getAddress().getAddress()).replace(".", "_");
        if (conf.get("user." + ip) != null)
            playerName = conf.getString("user." + ip);

        ServerPing respond = event.getResponse();
        respond.setDescription(ChatColor.translateAlternateColorCodes('&', "&5&lWelcome &7" + (playerName.equals("MHF_Question") ? "New Player" : playerName) + " &5&lto &7VaultCraft" +
                "\n&5&lWe are currently in &7Beta&5&l!"));

        /*if (release < System.currentTimeMillis())
            respond.setDescription(ChatColor.translateAlternateColorCodes('&', "&7-=&d*&7=- &5VaultCraft &d- &5ALPHA &7-=&d*&7=-\n&aWelcome!"));
        else
            respond.setDescription(ChatColor.translateAlternateColorCodes('&', "&7-=&d*&7=- &5VaultCraft &d- &5ALPHA &7-=&d*&7=-\n" + HHMMSS(release - System.currentTimeMillis())));*/
    }

    private static String getIp(InetAddress address) {
        byte[] arr = address.getAddress();
        String build = "";
        for (byte b : arr) {
            build += b + ".";
        }

        return build.hashCode() + "";
    }

    final static DecimalFormat df = new DecimalFormat("00");

    private static String HHMMSS(long in) {
        in = in/1000;
        long hrs = in/3600;
        in = in % 3600;
        long mns = in/60;
        in = in % 60;
        return df.format(hrs)+":"+df.format(mns)+":"+df.format(in);
    }
}
