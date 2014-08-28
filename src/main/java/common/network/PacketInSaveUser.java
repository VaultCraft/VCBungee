package common.network;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * Created by tacticalsk8er on 8/22/2014.
 */
public class PacketInSaveUser implements Packet, Serializable {

    private UserInfo userInfo;
    private String uuid;
    private String serverName;

    public PacketInSaveUser(UserInfo userInfo, String uuid, String serverName) {
        this.userInfo = userInfo;
        this.uuid = uuid;
        this.serverName = serverName;
    }

    @Override
    public PacketInSaveUser getType() {
        return this;
    }

    @Override
    public void run(ChannelHandlerContext chx, String clientName) {
        userInfo.saveUser(uuid, serverName);
    }
}
