package net.vaultcraft.vcbungee.network;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.vaultcraft.vcbungee.listeners.BungeeListener;
import net.vaultcraft.vcbungee.user.NetworkUser;
import net.vaultcraft.vcbungee.user.UserInfo;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;

/**
 * Created by tacticalsk8er on 8/16/2014.
 */
public class ClientReceiveThread implements Runnable {

    private Socket client;
    private ObjectInputStream in;

    private boolean start = false;
    private String name;

    public ClientReceiveThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true)  {
            try {
                Packet packet = (Packet) in.readObject();
                if(packet == null)
                    continue;
                if(!start) {
                    if (packet.getCommandType() == Packet.CommandType.START) {
                        start(packet.getDataStream());
                    }
                    continue;
                }
                switch (packet.getCommandType()) {
                    case USER:
                        if(!packet.getChannel().isEmpty())
                            user(packet.getChannel(), packet.getDataStream());
                        break;
                    case SEND:
                        if(packet.getChannel().isEmpty())
                            send(packet.getDataStream());
                        else
                            send(packet.getChannel(), packet.getDataStream());
                        break;
                    case SENDALL:
                        if(packet.getChannel().isEmpty())
                            sendAll(packet.getDataStream());
                        else
                            sendAll(packet.getChannel(), packet.getDataStream());
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void start(ObjectInputStream stream) {
        try {
            this.name = stream.readUTF();
            ServerMessageHandler.addClientName(client, this.name);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void user(String channel, ObjectInputStream stream) {
        String uuid;
        String serverName;
        ByteArrayDataOutput out;
        ByteArrayOutputStream outStream;
        ObjectOutputStream objOut;
        UserInfo userInfo;
        Packet packet;
        switch (channel.toLowerCase()) {
            case "get":
                try {
                    uuid = stream.readUTF();
                    serverName = stream.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                NetworkUser user = NetworkUser.fromUUID(uuid);
                if(user.isInUse()) {
                    BungeeListener.addWaiting(uuid + " " + serverName + " " + name);
                }
                userInfo = new UserInfo(serverName, user);
                outStream = new ByteArrayOutputStream();
                try {
                    objOut = new ObjectOutputStream(outStream);
                    objOut.writeUTF(uuid);
                    objOut.writeObject(userInfo);
                    objOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                packet = new Packet(Packet.CommandType.USER, channel, outStream.toByteArray());
                ServerMessageHandler.sendPacket(name, packet);
                user.setOnlineServer(name);
                user.setInUse(true);
                break;
            case "send":
                try {
                    uuid = stream.readUTF();
                    serverName = stream.readUTF();
                    userInfo = (UserInfo) stream.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
                userInfo.updateUser(uuid, serverName);
                break;
            case "update":
                try {
                    uuid = stream.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                serverName = NetworkUser.fromUUID(uuid).getOnlineServer();
                try {
                    packet = new Packet(Packet.CommandType.USER, channel, IOUtils.toByteArray(stream));
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                ServerMessageHandler.sendPacket(serverName, packet);
                break;
            case "isonline":
                try {
                    uuid = stream.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                boolean online = NetworkUser.fromUUID(uuid) != null;
                out = ByteStreams.newDataOutput();
                out.writeBoolean(online);
                packet = new Packet(Packet.CommandType.USER, channel, out.toByteArray());
                ServerMessageHandler.sendPacket(name, packet);
                break;
        }
    }

    private void send(String channel, ObjectInputStream stream) {
        try {
            String name = stream.readUTF();
            Packet packet = new Packet(Packet.CommandType.SEND, channel, IOUtils.toByteArray(stream));
            ServerMessageHandler.sendPacket(name, packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(ObjectInputStream stream) {
        try {
            String name = stream.readUTF();
            Packet packet = new Packet(Packet.CommandType.SEND, IOUtils.toByteArray(stream));
            ServerMessageHandler.sendPacket(name, packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAll(String channel, ObjectInputStream stream) {
        try {
            Packet packet = new Packet(Packet.CommandType.SEND, channel, IOUtils.toByteArray(stream));
            ServerMessageHandler.sendPacketToAll(client, packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAll(ObjectInputStream stream) {
        try {
            Packet packet = new Packet(Packet.CommandType.SEND, IOUtils.toByteArray(stream));
            ServerMessageHandler.sendPacketToAll(client, packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isStart() {
        return start;
    }

    public String getName() {
        return name;
    }
}
