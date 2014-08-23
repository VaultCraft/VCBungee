package common.network;

import java.net.Socket;

/**
 * Created by tacticalsk8er on 8/22/2014.
 */
public class PacketInSaveUser implements Packet {

    private UserInfo userInfo;
    private String uuid;
    private String serverName;

    public PacketInSaveUser(UserInfo userInfo, String uuid, String serverName) {
        this.userInfo = userInfo;
        this.uuid = uuid;
        this.serverName = serverName;
    }

    @Override
    public PacketInSaveUser getType() {
        return this;
    }

    @Override
    public void run(Socket socket, String clientName) {
        userInfo.saveUser(uuid, serverName);
    }
}
