package common.network;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketOutUserGet implements Packet, Serializable {

    private String uuid;
    private UserInfo info;

    public PacketOutUserGet(String uuid, UserInfo info) {
        this.uuid = uuid;
        this.info = info;
    }

    @Override
    public PacketOutUserGet getType() {
        return this;
    }

    @Override
    public void run(ChannelHandlerContext chx, String clientName) {
        //Used for clients
    }
}
