package common.network;

import net.md_5.bungee.api.ProxyServer;
import net.vaultcraft.vcbungee.user.NetworkUser;
import net.vaultcraft.vcbungee.user.UserNotInUseEvent;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class UserInfo implements Serializable{

    private int group = 0;
    private boolean banned = false;
    private Date tempBan = null;
    private boolean muted = false;
    private Date tempMute = null;

    private double money = 0;
    private int tokens = 0;

    private HashMap<String, String> globalUserdata = new HashMap<>();
    private HashMap<String, String> userdata = new HashMap<>();

    public UserInfo(String serverName, NetworkUser user) {
        this.group = user.getGroup();
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
        user.setGroup(group);
        user.setBanned(banned);
        user.setTempBan(tempBan);
        user.setMuted(muted);
        user.setTempMute(tempMute);
        user.setMoney(serverName, money);
        user.setTokens(tokens);
        user.setGlobalUserdata(globalUserdata);
        user.setUserdata(serverName, userdata);
        ProxyServer.getInstance().getPluginManager().callEvent(new UserNotInUseEvent(user));
    }

}
