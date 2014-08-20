package common.network;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public interface CallbackPacket<T> extends Packet {
    public void callback(T t);
}
