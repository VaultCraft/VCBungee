package net.vaultcraft.vcbungee.network;

import common.network.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by tacticalsk8er on 8/27/2014.
 */
public class MessageServer {

    private static volatile ConcurrentHashMap<String, Channel> channelNames = new ConcurrentHashMap<>();
    private static ChannelFuture future;
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private int port;

    public MessageServer(int port) {
        this.port = port;
    }

    public void init() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(Packet.class.getClassLoader())), new MessageServerHandler());
                        ProxyServer.getInstance().getLogger().info("New connection found.");
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

        future = bootstrap.bind(port).awaitUninterruptibly();

        assert future.isDone();

        if (future.isSuccess()) {
            ProxyServer.getInstance().getLogger().info("Message Server Started");
        } else {
            ProxyServer.getInstance().getLogger().info("Message Server did not start successfully... Shutting down proxy.");
            future.channel().closeFuture();
            ProxyServer.getInstance().stop();
        }
    }

    public int getPort() {
        return port;
    }

    public static void sendPacket(String clientName, Packet packet) {
        clientName = clientName.toLowerCase();
        Channel channel = channelNames.get(clientName);
        if (channel == null)
            return;
        channel.writeAndFlush(packet);
        ProxyServer.getInstance().getLogger().info("Packet sent to: " + clientName);
    }

    public static void sendPacketToAll(String clientName, Packet packet) {
        clientName = clientName.toLowerCase();
        for (String s : channelNames.keySet()) {
            if (!s.equals(clientName)) {
                channelNames.get(s).writeAndFlush(packet);
            }
        }
        ProxyServer.getInstance().getLogger().info("Packet sent to all connections.");
    }

    public static void addChannelName(String clientName, Channel channel) {
        clientName = clientName.toLowerCase();
        channelNames.put(clientName, channel);
    }

    public static void closeChannel(String clientName) {
        Channel channel = channelNames.get(clientName);
        if (channel == null)
            return;
        channelNames.remove(clientName);
        channel.close();
    }

    public static void close() {
        future.channel().closeFuture();
        for (String s : channelNames.keySet())
            closeChannel(s);
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
