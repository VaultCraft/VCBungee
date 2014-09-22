package common.network;

import net.md_5.bungee.api.ProxyServer;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.user.NetworkUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tacticalsk8er on 9/8/2014.
 */
public class OfflineUser implements Serializable{

    private ArrayList groups = new ArrayList<>();
    private boolean banned = false;
    private Date tempBan = null;
    private boolean muted = false;
    private Date tempMute = null;

    private int tokens = 0;

    private double money;
    private HashMap<String, String> globalUserdata = new HashMap<>();
    private HashMap<String,String> userdata = new HashMap<>();


    public OfflineUser(String UUID, String serverName) {
        NetworkUser user = NetworkUser.fromUUID(UUID);
        if(user == null) {
            ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), new Runnable() {
                @Override
                public void run() {

                }
            });
            return;
        }
        this.groups = user.getGroups();
        this.banned = user.isBanned();
        this.tempBan = user.getTempBan();
        this.muted = user.isMuted();
        this.tempMute = user.getTempMute();
        this.tokens = user.getTokens();
        this.money = user.getMoney(serverName);
        this.globalUserdata = user.getGlobalUserdata();
        this.userdata = user.getUserdata(serverName);
    }

    public ArrayList getGroups() {
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

    public int getTokens() {
        return tokens;
    }

    public double getMoney() {
        return money;
    }

    public HashMap<String, String> getGlobalUserdata() {
        return globalUserdata;
    }

    public HashMap<String, String> getUserdata() {
        return userdata;
    }

    public void setGroups(ArrayList groups) {
        this.groups = groups;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setTempBan(Date tempBan) {
        this.tempBan = tempBan;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setTempMute(Date tempMute) {
        this.tempMute = tempMute;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
