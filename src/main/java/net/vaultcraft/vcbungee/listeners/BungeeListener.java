package net.vaultcraft.vcbungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

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

        if (VCBungee.getInstance().getConfig().get("user."+ip) != null)
            return;

        VCBungee.getInstance().getConfig().set("user."+ip, name);
        VCBungee.getInstance().saveConfig();
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        NetworkUser.fromPlayer(event.getPlayer()).setDisconnected(true);
    }

    @EventHandler
    public void onPlayerPing(ProxyPingEvent event) throws IOException {
        Configuration conf = VCBungee.getInstance().getConfig();
        String playerName = "MHF_Question";

        String ip = getIp(event.getConnection().getAddress().getAddress()).replace(".", "_");
        if (conf.get("user."+ip) != null)
            playerName = conf.getString("user."+ip);

        URL url = new URL("https://minotar.net/avatar/"+playerName+"/64.png");

        BufferedImage img = ImageIO.read(url);

        ServerPing respond = event.getResponse();
        respond.setFavicon(Favicon.create(img));
        respond.setDescription(ChatColor.translateAlternateColorCodes('&', "&5&lWelcome &7"+(playerName.equals("MHF_Question") ? "New Player" : playerName)+" &5&lto &7VaultCraft"));
    }

    private static String getIp(InetAddress address) {
        byte[] arr = address.getAddress();
        String build = "";
        for (byte b : arr) {
            build+=b+".";
        }

        return build.hashCode()+"";
    }
}
