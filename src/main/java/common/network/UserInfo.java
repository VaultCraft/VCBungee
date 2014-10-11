package common.network;

import net.vaultcraft.vcbungee.user.NetworkUser;
import net.vaultcraft.vcbungee.user.UserReadyThread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class UserInfo implements Serializable {

    private List<Integer> groups = new ArrayList<>();
    private boolean banned = false;
    private Date tempBan = null;
    private boolean muted = false;
    private Date tempMute = null;

    private double money = 0;
    private int tokens = 0;

    private HashMap<String, String> globalUserdata = new HashMap<>();
    private HashMap<String, String> userdata = new HashMap<>();

    public UserInfo(String serverName, String uuid) {
        NetworkUser user = NetworkUser.fromUUID(uuid);
        this.groups = user.getGroups();
        this.banned = user.isBanned();
        this.tempBan = user.getTempBan();
        this.muted = user.isMuted();
        this.tempMute = user.getTempMute();
        this.money = user.getMoney(serverName);
        this.tokens = user.getTokens();
        this.globalUserdata = user.getGlobalUserdata();
        this.userdata = user.getUserdata(serverName);
    }

    public void updateUser(String uuid, String serverName) {
        NetworkUser user = NetworkUser.fromUUID(uuid);
        user.setGroups(groups);
        user.setBanned(banned);
        user.setTempBan(tempBan);
        user.setMuted(muted);
        user.setTempMute(tempMute);
        user.setMoney(serverName, money);
        user.setTokens(tokens);
        user.setGlobalUserdata(globalUserdata);
        user.setUserdata(serverName, userdata);
        if(user.isDisconnected()) {
            NetworkUser.remove(user.getPlayer());
        } else {
            UserReadyThread.addReadyUser(user);
            user.save();
        }
    }

    public void saveUser(String uuid, String serverName) {
        NetworkUser user = NetworkUser.fromUUID(uuid);
        user.setGroups(groups);
        user.setBanned(banned);
        user.setTempBan(tempBan);
        user.setMuted(muted);
        user.setTempMute(tempMute);
        user.setMoney(serverName, money);
        user.setTokens(tokens);
        user.setGlobalUserdata(globalUserdata);
        user.setUserdata(serverName, userdata);
        user.save();
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public boolean isBanned() {
        return banned;
    }

    public Date getTempBan() {
        return tempBan;
    }

    public boolean isMuted() {
        return muted;
    }

    public Date getTempMute() {
        return tempMute;
    }

    public double getMoney() {
        return money;
    }

    public int getTokens() {
        return tokens;
    }

    public HashMap<String, String> getGlobalUserdata() {
        return globalUserdata;
    }

    public HashMap<String, String> getUserdata() {
        return userdata;
    }

}
