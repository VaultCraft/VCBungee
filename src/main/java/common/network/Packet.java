package common.network;

import java.net.Socket;

/**
 * Created by tacticalsk8er on 8/18/2014.
 */
public interface Packet<T>{
    public T getType();

    public void run(Socket socket, String clientName);
}
