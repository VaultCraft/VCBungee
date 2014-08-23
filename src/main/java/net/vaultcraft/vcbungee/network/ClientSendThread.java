package net.vaultcraft.vcbungee.network;

import common.network.Packet;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by tacticalsk8er on 8/18/2014.
 */
public class ClientSendThread implements Runnable {

    private Socket client;

    private ConcurrentLinkedQueue<Packet> packets = new ConcurrentLinkedQueue<>();
    private String name;
    private boolean running = true;

    public ClientSendThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            while(running) {
                if(packets.size() > 0) {
                    Packet packet = packets.poll();
                    out.writeObject(packet);
                    ProxyServer.getInstance().getLogger().info("Message sent to " + name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            running = false;
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPacket(Packet packet) {
        ProxyServer.getInstance().getLogger().info("Packet added to que of " + name);
        packets.add(packet);
    }

}
