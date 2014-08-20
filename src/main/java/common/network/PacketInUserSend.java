package common.network;

import java.io.Serializable;
import java.net.Socket;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class PacketInUserSend implements Packet, Serializable {

    private String uuid;
    private String serverName;
    private UserInfo info;

    public PacketInUserSend(String uuid, String serverName, UserInfo info) {
        this.uuid = uuid;
        this.serverName = serverName;
        this.info = info;
    }

    @Override
    public PacketInUserSend getType() {
        return null;
    }

    @Override
    public void run(Socket socket, String clientName) {
        info.updateUser(uuid, serverName);
    }
}
