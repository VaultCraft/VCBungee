package net.vaultcraft.vcbungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.user.NetworkUser;

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
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        NetworkUser.fromPlayer(event.getPlayer()).setDisconnected(true);
    }
}
