package net.vaultcraft.vcbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.vaultcraft.vcbungee.config.ClassConfig;
import net.vaultcraft.vcbungee.database.mongo.MongoDB;
import net.vaultcraft.vcbungee.database.mongo.MongoInfo;
import net.vaultcraft.vcbungee.network.ServerMessageHandler;
import net.vaultcraft.vcbungee.user.NetworkUser;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tacticalsk8er on 8/16/2014.
 */
public class VCBungee extends Plugin {

    @ClassConfig.Config(path = "Port")
    public static int port = 25566;
    @ClassConfig.Config(path = "Servers")
    public static List<String> servers = new ArrayList<>(Arrays.asList("hub", "prison"));

    private static VCBungee instance;
    private Configuration configuration;
    private ServerMessageHandler server;
    private MongoDB mongoDB;

    @Override
    public void onEnable() {
        instance = this;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClassConfig.loadConfig(MongoInfo.class, configuration);
        ClassConfig.loadConfig(this.getClass(), configuration);
        ClassConfig.updateConfig(MongoInfo.class, configuration);
        ClassConfig.updateConfig(this.getClass(), configuration);
        this.saveConfig();

        server = new ServerMessageHandler(port);

        try {
            mongoDB = new MongoDB(MongoInfo.host, MongoInfo.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        NetworkUser.disable();
        server.close();
        mongoDB.close();
    }

    public static VCBungee getInstance() {
        return instance;
    }

    public Configuration getConfig() {
        return configuration;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MongoDB getMongoDB() {
        return mongoDB;
    }

    public ServerMessageHandler getServer() {
        return server;
    }
}
