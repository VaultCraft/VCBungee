package common.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Created by tacticalsk8er on 8/18/2014.
 */
public class Packet implements Serializable{

    private CommandType commandType;
    private String channel;
    private byte[] data;

    public Packet(CommandType commandType, byte[] data) {
        this.commandType = commandType;
        this.channel = "";
        this.data = data;
    }

    public Packet(CommandType commandType, String channel, byte[] data) {
        this.commandType = commandType;
        this.channel = channel;
        this.data = data;
    }

    public enum CommandType {
        START,
        USER,
        SEND,
        SENDALL;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }

    public ObjectInputStream getDataStream() {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
