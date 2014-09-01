package common.network;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by tacticalsk8er on 8/29/2014.
 */
public class PacketOutEnd implements Packet {

    @Override
    public PacketOutEnd getType() {
        return this;
    }

    @Override
    public void run(ChannelHandlerContext chx, String clientName) {
        //Client Only
    }
}
