package common.network;

import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.api.ProxyServer;
import net.vaultcraft.vcbungee.network.MessageServer;

import java.io.Serializable;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketInStart implements Packet, Serializable {

    private String name;

    public PacketInStart(String name) {
        this.name = name;
    }

    @Override
    public PacketInStart getType() {
        return this;
    }

    @Override
    public void run(ChannelHandlerContext chx, String clientName) {
        MessageServer.addChannelName(name, chx.channel());
        ProxyServer.getInstance().getLogger().info("Completed connection from: " + name);
    }

    public String getName() {
        return name;
    }
}
