package net.vaultcraft.vcbungee.network;

import common.network.Packet;
import common.network.PacketInStart;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.md_5.bungee.api.ProxyServer;

/**
 * Created by tacticalsk8er on 8/27/2014.
 */
public class MessageServerHandler extends ChannelInboundHandlerAdapter {

    private String name;
    private boolean init = false;

    @Override
    public void channelRead(ChannelHandlerContext chx, Object msg)  {
        if (!init) {
            if (!(msg instanceof PacketInStart))
                return;
            PacketInStart packet = (PacketInStart) msg;
            this.name = packet.getName();
            init = true;
        }
        ((Packet) msg).run(chx, name);
        ProxyServer.getInstance().getLogger().info("Packet received from: " + name);
    }

    @Override
    public void channelInactive(ChannelHandlerContext chx) {
        close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext chx) {
        close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void close() {
        ProxyServer.getInstance().getLogger().info("Lost connection to: " + name);
        MessageServer.closeChannel(name);
    }
}
