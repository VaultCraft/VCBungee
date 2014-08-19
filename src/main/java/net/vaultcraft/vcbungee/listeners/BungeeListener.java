package net.vaultcraft.vcbungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.vaultcraft.vcbungee.VCBungee;
import common.network.Packet;
import net.vaultcraft.vcbungee.network.ServerMessageHandler;
import net.vaultcraft.vcbungee.user.NetworkUser;
import common.network.UserInfo;
import net.vaultcraft.vcbungee.user.UserNotInUseEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class BungeeListener implements Listener {

    private static List<String> waitingList = new ArrayList<>();

    public BungeeListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(VCBungee.getInstance(), this);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        new NetworkUser(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        NetworkUser.remove(event.getPlayer());
        for(int i = 0; i < waitingList.size(); i++) {
            String[] parts = waitingList.get(i).split(" ");
            if(event.getPlayer().getUniqueId().toString().equals(parts[0]))
                waitingList.remove(i);
        }
    }

    @EventHandler
    public void onNotInUse(UserNotInUseEvent event) {
        NetworkUser user = event.getUser();
        for(String s : waitingList) {
            String[] parts = s.split(" ");
            if(!user.getPlayer().getUniqueId().toString().equals(parts[0]))
                continue;
            UserInfo userInfo = new UserInfo(parts[1], user);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream objOut = new ObjectOutputStream(outStream);
                objOut.writeUTF(user.getPlayer().getUniqueId().toString());
                objOut.writeObject(userInfo);
                objOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Packet packet = new Packet(Packet.CommandType.USER, "get", outStream.toByteArray());
            ServerMessageHandler.sendPacket(parts[2], packet);
            user.setOnlineServer(parts[2]);
            user.setInUse(true);
            break;
        }
    }

    public static void addWaiting(String uuidServerClient) {
        waitingList.add(uuidServerClient);
    }
}
