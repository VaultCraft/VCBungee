package common.network;

import io.netty.channel.ChannelHandlerContext;
import net.vaultcraft.vcbungee.user.UserReadyThread;

import java.io.Serializable;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketInUserGet implements Packet, Serializable {

    private String uuid;
    private String serverName;

    public PacketInUserGet(String uuid, String serverName) {
        this.uuid = uuid;
        this.serverName = serverName;
    }

    @Override
    public PacketInUserGet getType() {
        return this;
    }

    @Override
    public void run(ChannelHandlerContext chx, String clientName) {
        UserReadyThread.addWaiting(uuid + " " + serverName + " " + clientName);
    }
}
