package net.vaultcraft.vcbungee.user;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vaultcraft.vcbungee.VCBungee;

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
    private boolean disconnected = false;

    private List<Integer> groups = new ArrayList<>();
    private boolean banned = false;
    private Date tempBan = null;
    private boolean muted = false;
    private Date tempMute = null;

    private int tokens = 0;

    private HashMap<String, Double> moneyList = new HashMap<>();
    private HashMap<String, String> globalUserdata = new HashMap<>();
    private HashMap<String, HashMap<String, String>> userdataMap = new HashMap<>();

    public NetworkUser(final ProxiedPlayer player) {
        this.player = player;
        try {
            this.uuid = UUIDFetcher.getUUIDOf(player.getName()).toString();
        } catch (Exception e) {
            player.disconnect("Error in getting your UUID. Notify Admins.");
            e.printStackTrace();
        }
        async_player_map.put(player, this);
        async_uuid_map.put(this.uuid, this);
        ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                DBObject dbObject = VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", uuid);
                if (dbObject != null) {
                    String groupList = dbObject.get("Group") == null ? "1" : (String) dbObject.get("Group");
                    groups = parseGroups(groupList);
                    banned = dbObject.get("Banned") == null ? false : (Boolean) dbObject.get("Banned");
                    tempBan = (Date) dbObject.get("TempBan");
                    muted = dbObject.get("Muted") == null ? false : (Boolean) dbObject.get("Muted");
                    tempMute = (Date) dbObject.get("TempMute");
                    for (String serverName : VCBungee.servers) {
                        Object o = dbObject.get(serverName + "-Money");
                        double value = (o == null ? 0 : (o instanceof Double ? (Double) o : (Integer) o));
                        double money = dbObject.get(serverName + "-Money") == null ? 0 : value;
                        moneyList.put(serverName, money);
                        HashMap<String, String> userdata = dbObject.get(serverName + "-UserData") == null ? new HashMap<String, String>() : parseData((String) dbObject.get(serverName + "-UserData"));
                        userdataMap.put(serverName, userdata);
                    }

                    tokens = dbObject.get("Tokens") == null ? 0 : (Integer) dbObject.get("Tokens");
                    globalUserdata = dbObject.get("Global-UserData") == null ? new HashMap<String, String>() : parseData((String) dbObject.get("Global-UserData"));
                } else {
                    for (String serverName : VCBungee.servers) {
                        moneyList.put(serverName, 0.0);
                        userdataMap.put(serverName, new HashMap<String, String>());
                    }
                }
                UserReadyThread.addReadyUser(NetworkUser.this);
            }
        });
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public boolean isMuted() {
        return muted;
    }

    public boolean isBanned() {
        return banned;
    }

    public Date getTempBan() {
        return tempBan;
    }

    public Date getTempMute() {
        return tempMute;
    }

    public int getTokens() {
        return tokens;
    }

    public HashMap<String, String> getGlobalUserdata() {
        return globalUserdata;
    }

    public double getMoney(String serverName) {
        return moneyList.get(serverName);
    }

    public HashMap<String, String> getUserdata(String serverName) {
        return userdataMap.get(serverName);
    }

    public String getOnlineServer() {
        return onlineServer;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setTempBan(Date tempBan) {
        this.tempBan = tempBan;
    }

    public void setTempMute(Date tempMute) {
        this.tempMute = tempMute;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void setGlobalUserdata(HashMap<String, String> globalUserdata) {
        this.globalUserdata = globalUserdata;
    }

    public void setMoney(String serverName, double money) {
        if(moneyList.containsKey(serverName))
            moneyList.remove(serverName);
        moneyList.put(serverName, money);
    }

    public void setUserdata(String serverName, HashMap<String, String> userdata) {
        if(userdataMap.containsKey(serverName))
            userdataMap.remove(serverName);
        userdataMap.put(serverName, userdata);
    }

    public void setOnlineServer(String onlineServer) {
        this.onlineServer = onlineServer;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public void save() {
        ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                DBObject dbObject = VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", getUuid()) == null ? new BasicDBObject() : VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", getUuid());
                dbObject.put("UUID", getUuid());
                dbObject.put("Group", groupsToString(getGroups()));
                dbObject.put("Banned", isBanned());
                dbObject.put("TempBan", getTempBan());
                dbObject.put("Muted", isMuted());
                dbObject.put("TempMute", getTempMute());
                for (String serverName : VCBungee.servers) {
                    dbObject.put(serverName + "-Money", getMoney(serverName));
                    dbObject.put(serverName + "-UserData", dataToString(getUserdata(serverName)));
                }
                dbObject.put("Tokens", getTokens());
                dbObject.put("Global-UserData", dataToString(getGlobalUserdata()));
                DBObject dbObject1 = VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", getUuid());
                if (dbObject1 == null)
                    VCBungee.getInstance().getMongoDB().insert("VaultCraft", "Users", dbObject);
                else
                    VCBungee.getInstance().getMongoDB().update("VaultCraft", "Users", dbObject1, dbObject);
            }
        });
    }

    public static void remove(final ProxiedPlayer player) {
        final NetworkUser user = async_player_map.get(player);
        ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                DBObject dbObject = VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", user.getUuid()) == null ? new BasicDBObject() : VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", user.getUuid());
                dbObject.put("UUID", user.getUuid());
                dbObject.put("Group", groupsToString(user.getGroups()));
                dbObject.put("Banned", user.isBanned());
                dbObject.put("TempBan", user.getTempBan());
                dbObject.put("Muted", user.isMuted());
                dbObject.put("TempMute", user.getTempMute());
                for (String serverName : VCBungee.servers) {
                    dbObject.put(serverName + "-Money", user.getMoney(serverName));
                    dbObject.put(serverName + "-UserData", dataToString(user.getUserdata(serverName)));
                }
                dbObject.put("Tokens", user.getTokens());
                dbObject.put("Global-UserData", dataToString(user.getGlobalUserdata()));
                DBObject dbObject1 = VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", user.getUuid());
                if (dbObject1 == null)
                    VCBungee.getInstance().getMongoDB().insert("VaultCraft", "Users", dbObject);
                else
                    VCBungee.getInstance().getMongoDB().update("VaultCraft", "Users", dbObject1, dbObject);
            }
        });
        async_player_map.remove(player);
    }

    public static void disable() {
        for (NetworkUser user : async_player_map.values()) {
            DBObject dbObject = VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", user.getUuid()) == null ? new BasicDBObject() : VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", user.getUuid());
            dbObject.put("UUID", user.getUuid());
            dbObject.put("Group", groupsToString(user.getGroups()));
            dbObject.put("Banned", user.isBanned());
            dbObject.put("TempBan", user.getTempBan());
            dbObject.put("Muted", user.isMuted());
            dbObject.put("TempMute", user.getTempMute());
            for (String serverName : VCBungee.servers) {
                dbObject.put(serverName + "-Money", user.getMoney(serverName));
                dbObject.put(serverName + "-UserData", dataToString(user.getUserdata(serverName)));
            }
            dbObject.put("Tokens", user.getTokens());
            dbObject.put("Global-UserData", dataToString(user.getGlobalUserdata()));
            DBObject dbObject1 = VCBungee.getInstance().getMongoDB().query("VaultCraft", "Users", "UUID", user.getUuid());
            if (dbObject1 == null)
                VCBungee.getInstance().getMongoDB().insert("VaultCraft", "Users", dbObject);
            else
                VCBungee.getInstance().getMongoDB().update("VaultCraft", "Users", dbObject1, dbObject);
        }
    }

    private static String dataToString(HashMap<String, String> userdata) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (Map.Entry entry : userdata.entrySet()) {
            sb.append(entry.getKey()).append("▲").append(entry.getValue());
            if (userdata.size() - 1 != counter)
                sb.append("▼");
            counter++;
        }
        return sb.toString();
    }

    private static HashMap<String, String> parseData(String data) {
        HashMap<String, String> userdata = new HashMap<>();
        if (!(data.contains("▲")))
            return userdata;
        if(data.contains("▼")) {
            String[] parts = data.split("▼");
            for (String s : parts) {
                String[] entry = s.split("▲");
                userdata.put(entry[0], entry[1]);
            }
        } else {
            String[] parts = data.split("▲");
            userdata.put(parts[0], parts[1]);
        }
        return userdata;
    }

    private static List<Integer> parseGroups(String s) {
        List<Integer> groups = new ArrayList<>();
        String[] parts = s.split(",");
        for(String part : parts) {
            try {
                groups.add(Integer.parseInt(part));
            } catch(NumberFormatException ignored) {
            }
        }
        return groups;
    }

    private static String groupsToString(List<Integer> groups) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < groups.size(); i++) {
            if(i + 1 == groups.size())
                sb.append(groups.get(i));
            else
                sb.append(groups.get(i)).append(",");
        }
        return sb.toString();
    }
}
