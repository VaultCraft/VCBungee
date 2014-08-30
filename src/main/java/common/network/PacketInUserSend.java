package common.network;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketInUserSend implements Packet, Serializable {

    private String uuid;
    private String serverName;
    private UserInfo info;

    public PacketInUserSend(String uuid, String serverName, UserInfo info) {
        this.uuid = uuid;
        this.serverName = serverName;
        this.info = info;
    }

    @Override
    public PacketInUserSend getType() {
        return null;
    }

    @Override
    public void run(ChannelHandlerContext chx, String clientName) {
        info.updateUser(uuid, serverName);
    }
}
