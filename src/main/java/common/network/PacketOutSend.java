package common.network;

import java.io.Serializable;
import java.net.Socket;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketOutSend implements Packet, Serializable {

    private String channel;
    private byte[] data;

    public PacketOutSend(PacketInSend packetInSend) {
        this.channel = packetInSend.getChannel();
        this.data = packetInSend.getData();
    }

    public PacketOutSend(PacketInSendAll packetInSendAll) {
        this.channel = packetInSendAll.getChannel();
        this.data = packetInSendAll.getData();
    }

    @Override
    public PacketOutSend getType() {
        return this;
    }

    @Override
    public void run(Socket socket, String clientName) {
        //PacketOut: Client Only
    }
}
