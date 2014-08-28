package common.network;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * Created by tacticalsk8er on 8/27/2014.
 */
public class PacketOutVote implements Packet, Serializable {

    private String username;

    public PacketOutVote(String username) {
        this.username = username;
    }

    @Override
    public PacketOutVote getType() {
        return this;
    }

    @Override
    public void run(ChannelHandlerContext chx, String clientName) {
        //Client Only
    }
}
