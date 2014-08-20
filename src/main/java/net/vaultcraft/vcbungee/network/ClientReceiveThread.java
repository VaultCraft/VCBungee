package net.vaultcraft.vcbungee.network;

import common.network.Packet;
import common.network.PacketInStart;

import java.io.IOException;
import java.io.ObjectInputStream;
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
        while (true) {
            if (!client.isConnected() || client.isInputShutdown() || client.isOutputShutdown() || client.isClosed()) {
                break;
            }

            try {
                Packet packet = (Packet) in.readObject();
                if (packet == null)
                    continue;
                if (!start) {
                    if (!(packet instanceof PacketInStart))
                        continue;
                    packet.run(client, name);
                    continue;
                }
                packet.run(client, name);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isStart() {
        return start;
    }

    public String getName() {
        return name;
    }
}
