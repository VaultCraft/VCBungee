package common.network;

import net.md_5.bungee.api.ProxyServer;
import net.vaultcraft.vcbungee.network.ServerMessageHandler;

import java.io.Serializable;
import java.net.Socket;

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
    public void run(Socket client, String clientName) {
        ServerMessageHandler.addClientName(client, this.name);
        ProxyServer.getInstance().getLogger().info("Client " + name + " is ready.");
    }
}
