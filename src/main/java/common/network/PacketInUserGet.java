package common.network;

import net.vaultcraft.vcbungee.network.ServerMessageHandler;
import net.vaultcraft.vcbungee.user.NetworkUser;

import java.io.Serializable;
import java.net.Socket;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketInUserGet implements CallbackPacket, Serializable {

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
    public void run(Socket socket, String clientName) {
        NetworkUser user = NetworkUser.fromUUID(uuid);
        PacketOutUserGet packetOutUserGet = new PacketOutUserGet(uuid, new UserInfo(serverName, user));
        ServerMessageHandler.sendPacket(clientName, packetOutUserGet);
    }

    @Override
    public void callback(Object o) {
        //Used for clients
    }
}
