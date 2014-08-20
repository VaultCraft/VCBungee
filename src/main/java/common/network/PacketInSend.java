package common.network;

import net.vaultcraft.vcbungee.network.ServerMessageHandler;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketInSend implements Packet, Serializable{

    private String serverName;
    private String channel;
    private byte[] data;

    public PacketInSend(String serverName, String channel, ByteArrayOutputStream out) {
        this.serverName = serverName;
        this.channel = channel;
        this.data = out.toByteArray();
    }

    public PacketInSend(String serverName, ByteArrayOutputStream out) {
        this(serverName, "", out);
    }

    @Override
    public PacketInSend getType() {
        return this;
    }

    @Override
    public void run(Socket socket, String clientName) {
        PacketOutSend packetOutSend = new PacketOutSend(this);
        ServerMessageHandler.sendPacket(serverName, packetOutSend);
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }
}
