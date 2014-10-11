package net.vaultcraft.vcbungee.user;

import common.network.PacketOutUserGet;
import common.network.UserInfo;
import net.md_5.bungee.api.ProxyServer;
import net.vaultcraft.vcbungee.network.MessageServer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tacticalsk8er on 8/21/2014.
 */
public class UserReadyThread implements Runnable {

    private static volatile ConcurrentHashMap<UUID, String> waitingList = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<UUID, NetworkUser> readyUsers = new ConcurrentHashMap<>();

    @Override
    public void run() {
        while(true) {
            for (UUID userUUID : readyUsers.keySet()) {
                NetworkUser user = readyUsers.get(userUUID);
                for (UUID uuid : waitingList.keySet()) {
                    String[] parts = waitingList.get(uuid).split(" ");
                    if (!user.getUuid().equals(parts[0]))
                        continue;
                    UserInfo userInfo = new UserInfo(parts[1], user.getUuid());
                    MessageServer.sendPacket(parts[2], new PacketOutUserGet(user.getUuid(), userInfo));
                    readyUsers.remove(userUUID);
                    waitingList.remove(uuid);
                }
            }
        }
    }

    public static void addWaiting(String uuidServerClient) {
        ProxyServer.getInstance().getLogger().info("Waiter added: " + uuidServerClient);
        waitingList.put(UUID.randomUUID(), uuidServerClient);
    }

    public static void addReadyUser(NetworkUser user) {
        ProxyServer.getInstance().getLogger().info("User added: " + user.getUuid());
        readyUsers.put(UUID.randomUUID(), user);
    }

    public static ConcurrentHashMap<UUID, String> getWaitingList() {
        return waitingList;
    }
}
