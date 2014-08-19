package net.vaultcraft.vcbungee.network;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.vaultcraft.vcbungee.VCBungee;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tacticalsk8er on 8/16/2014.
 */
public class ServerMessageHandler implements Runnable {

    private static volatile ConcurrentHashMap<Socket, ClientSendThread> clientSendThreads = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<Socket, ClientReceiveThread> clientReceiveThreads = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<Socket, List<ScheduledTask>> clientTasks = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<String, Socket> clientNames = new ConcurrentHashMap<>();

    private ServerSocket messageServer;
    private boolean running = true;

    public ServerMessageHandler(int port) {
        try {
            messageServer = new ServerSocket(port);
            messageServer.setSoTimeout(5000);
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().severe("Error while starting up Message Server! Shutting down proxy.");
            e.printStackTrace();
            ProxyServer.getInstance().stop();
        }
    }

    public static void addClientName(Socket client, String name) {
        clientNames.put(name, client);
    }


    @Override
    public void run() {
        while (running) {
            for (Socket socket : clientTasks.keySet()) {
                if (!socket.isConnected()) {
                    for(ScheduledTask task : clientTasks.get(socket))
                        task.cancel();
                    clientTasks.remove(socket);
                    ClientReceiveThread clientThread = clientReceiveThreads.get(socket);
                    if (clientThread.isStart())
                        clientNames.remove(clientThread.getName());
                    clientReceiveThreads.remove(socket);
                    clientSendThreads.remove(socket);
                }
            }
            try {
                Socket client = messageServer.accept();
                ClientReceiveThread receiveThread = new ClientReceiveThread(client);
                ClientSendThread sendThread = new ClientSendThread(client);
                ScheduledTask receiveTask = ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), receiveThread);
                ScheduledTask sendTask = ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), sendThread);
                clientReceiveThreads.put(client, receiveThread);
                clientSendThreads.put(client, sendThread);
                List<ScheduledTask> tasks = new ArrayList<>(Arrays.asList(receiveTask, sendTask));
                clientTasks.put(client, tasks);
            } catch (SocketTimeoutException ignore) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendPacket(String clientName, Packet packet) {
        if(clientNames.contains(clientName)) {
            Socket socket = clientNames.get(clientName);
            clientSendThreads.get(socket).addPacket(packet);
        }
    }

    public static void sendPacketToAll(Socket sender, Packet packet) {
        for(Socket socket : clientSendThreads.keySet()) {
            if(socket.equals(sender))
                continue;
            clientSendThreads.get(socket).addPacket(packet);
        }
    }

    public void close() {
        for(Socket socket : clientTasks.keySet()) {
            List<ScheduledTask> scheduledTasks = clientTasks.get(socket);
            for(ScheduledTask task : scheduledTasks)
                task.cancel();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clientReceiveThreads.clear();
        clientSendThreads.clear();
        clientTasks.clear();
        clientNames.clear();
        try {
            messageServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
