package net.vaultcraft.vcbungee.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tacticalsk8er on 8/18/2014.
 */
public class ClientSendThread implements Runnable {

    private Socket client;

    private List<Packet> packets = new ArrayList<>();

    public ClientSendThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            while(true) {
                if(packets.size() > 0) {
                    Packet packet = packets.get(0);
                    out.writeObject(packet);
                    packets.remove(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPacket(Packet packet) {
        packets.add(packet);
    }

}
