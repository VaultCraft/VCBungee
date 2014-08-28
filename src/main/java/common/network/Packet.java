package common.network;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by tacticalsk8er on 8/18/2014.
 */
public interface Packet<T>{
    public T getType();

    public void run(ChannelHandlerContext chx, String clientName);
}
