package net.vaultcraft.vcbungee.network;

import common.network.Packet;
import common.network.PacketInStart;
import net.md_5.bungee.api.ProxyServer;

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
    private boolean running = true;
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
            running = false;
        }
        while (running) {
            try {
                Packet packet = (Packet) in.readObject();
                if (packet == null)
                    continue;
                if (!start) {
                    if (!(packet instanceof PacketInStart))
                        continue;
                    packet.run(client, name);
                    start = true;
                    continue;
                }
                ProxyServer.getInstance().getLogger().info("Messaged Received from " + name);
                packet.run(client, name);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStart() {
        return start;
    }

    public String getName() {
        return name;
    }
}
