package common.network;

import io.netty.channel.ChannelHandlerContext;
import net.vaultcraft.vcbungee.network.MessageServer;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketInSendAll implements Packet, Serializable {

    private String channel;
    private byte[] data;

    public PacketInSendAll(String channel, ByteArrayOutputStream out) {
        this.channel = channel;
        this.data = out.toByteArray();
    }

    public PacketInSendAll(ByteArrayOutputStream out) {
        this("", out);
    }

    @Override
    public PacketInSendAll getType() {
        return this;
    }

    @Override
    public void run(ChannelHandlerContext chx, String clientName) {
        PacketOutSend packetOutSend = new PacketOutSend(this);
        MessageServer.sendPacketToAll(clientName, packetOutSend);
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }
}
