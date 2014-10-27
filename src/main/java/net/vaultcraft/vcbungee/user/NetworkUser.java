package net.vaultcraft.vcbungee.user;

import com.mongodb.DBObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.commands.WhitelistCommand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tacticalsk8er on 8/17/2014.
 */
public class NetworkUser {

    private static volatile ConcurrentHashMap<String, NetworkUser> async_uuid_map = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<ProxiedPlayer, NetworkUser> async_player_map = new ConcurrentHashMap<>();

    public static NetworkUser fromPlayer(ProxiedPlayer player) {
        return async_player_map.get(player);
    }

    public static NetworkUser fromUUID(String UUID) {
        return async_uuid_map.get(UUID);
    }

    private ProxiedPlayer player;

    private String onlineServer = "";
    private String uuid;

    private List<Integer> groups = new ArrayList<>(Arrays.asList(1));

    public NetworkUser(final ProxiedPlayer player) {
        this.player = player;
        try {
            this.uuid = UUIDFetcher.getUUIDOf(player.getName()).toString();
        } catch (Exception e) {
            player.disconnect(new TextComponent("Error in getting your UUID. Notify Admins."));
            e.printStackTrace();
        }
        async_player_map.put(player, this);
        async_uuid_map.put(this.uuid, this);
        ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), () -> {
            DBObject dbObject = VCBungee.getInstance().getMongoDB().query(VCBungee.mongoDBName, "Users", "UUID", uuid);
            if (dbObject != null) {
                String groupList = dbObject.get("Group") == null ? "1" : (String) dbObject.get("Group");
                groups = parseGroups(groupList);
            }
            if (WhitelistCommand.isWhitelisted()) {
                if (!GroupUtil.hasPermission(groups, GroupUtil.Group.ADMIN) && WhitelistCommand.isHardMode() && !WhitelistCommand.getWhitelist().contains(uuid)) {
                    player.disconnect(new TextComponent("You are not whitelisted!"));
                    NetworkUser.remove(player);
                } else if (!GroupUtil.hasPermission(groups, GroupUtil.Group.HELPER) && !WhitelistCommand.getWhitelist().contains(uuid)) {
                    player.disconnect(new TextComponent("You are not whitelisted!"));
                    NetworkUser.remove(player);
                }
            }
        });
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public String getOnlineServer() {
        return onlineServer;
    }

    public String getUuid() {
        return uuid;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    public void setOnlineServer(String onlineServer) {
        this.onlineServer = onlineServer;
    }

    public static void remove(final ProxiedPlayer player) {
        final NetworkUser user = async_player_map.get(player);
        async_player_map.remove(player);
        async_uuid_map.remove(user.getUuid());
    }

    private static List<Integer> parseGroups(String s) {
        List<Integer> groups = new ArrayList<>();
        String[] parts = s.split(",");
        for (String part : parts) {
            try {
                groups.add(Integer.parseInt(part));
            } catch (NumberFormatException ignored) {
            }
        }
        return groups;
    }
}
