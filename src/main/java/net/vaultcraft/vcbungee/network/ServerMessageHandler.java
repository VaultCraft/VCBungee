package net.vaultcraft.vcbungee.network;

import common.network.Packet;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.vaultcraft.vcbungee.VCBungee;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tacticalsk8er on 8/16/2014.
 */
public class ServerMessageHandler implements Runnable {

    private static volatile ConcurrentHashMap<Socket, ClientSendThread> clientSendThreads = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<Socket, ClientReceiveThread> clientReceiveThreads = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<Socket, List<ScheduledTask>> clientTasks = new ConcurrentHashMap<>();
    private static HashMap<String, Socket> clientNames = new HashMap<>();

    private ServerSocket messageServer;
    private boolean running = true;

    public ServerMessageHandler(int port) {
        try {
            messageServer = new ServerSocket(port);
            messageServer.setSoTimeout(5000);
            ProxyServer.getInstance().getLogger().info("Message Server Started");
            ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), this);
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().severe("Error while starting up Message Server! Shutting down proxy.");
            e.printStackTrace();
            ProxyServer.getInstance().stop();
        }
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
                    clientThread.setRunning(false);
                    clientSendThreads.get(socket).setRunning(false);
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
                ProxyServer.getInstance().getLogger().info("Client connected");
            } catch (SocketTimeoutException ignore) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendPacket(String clientName, Packet packet) {
        if(clientNames.containsKey(clientName)) {
            ProxyServer.getInstance().getLogger().info("Starting to send a packet.");
            Socket socket = clientNames.get(clientName);
            clientSendThreads.get(socket).addPacket(packet);
        } else {
            ProxyServer.getInstance().getLogger().info("Name is not found: " + clientName);
        }
    }

    public static void setClientName(Socket socket, String name) {
        clientReceiveThreads.get(socket).setName(name);
        clientSendThreads.get(socket).setName(name);
        clientNames.put(name, socket);
        ProxyServer.getInstance().getLogger().info("Client Names: ");
        for(String clientName : clientNames.keySet())
            ProxyServer.getInstance().getLogger().info(clientName);
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
            clientReceiveThreads.get(socket).setRunning(false);
            clientSendThreads.get(socket).setRunning(false);
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
        running = false;
        try {
            messageServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
