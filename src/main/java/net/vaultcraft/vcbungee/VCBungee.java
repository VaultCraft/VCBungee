package net.vaultcraft.vcbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.vaultcraft.vcbungee.config.ClassConfig;
import net.vaultcraft.vcbungee.database.mongo.MongoDB;
import net.vaultcraft.vcbungee.database.mongo.MongoInfo;
import net.vaultcraft.vcbungee.listeners.BungeeListener;
import net.vaultcraft.vcbungee.network.ServerMessageHandler;
import net.vaultcraft.vcbungee.user.NetworkUser;
import net.vaultcraft.vcbungee.user.UserReadyThread;
import net.vaultcraft.vcbungee.vote.Votifier;

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

        if(!getDataFolder().exists())
            getDataFolder().mkdir();

        File configurationFile = new File(getDataFolder(), "config.yml");
        if (!configurationFile.exists()) {
            try {
                configurationFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClassConfig.loadConfig(MongoInfo.class, configuration);
        ClassConfig.loadConfig(this.getClass(), configuration);
        ClassConfig.loadConfig(Votifier.class, configuration);
        ClassConfig.updateConfig(MongoInfo.class, configuration);
        ClassConfig.updateConfig(this.getClass(), configuration);
        ClassConfig.updateConfig(Votifier.class, configuration);

        this.saveConfig();

        server = new ServerMessageHandler(port);
        new BungeeListener();

        Votifier votifier = new Votifier();
        votifier.onEnable();

        try {
            mongoDB = new MongoDB(MongoInfo.host, MongoInfo.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.getProxy().getScheduler().runAsync(this, new UserReadyThread());
    }

    @Override
    public void onDisable() {
        Votifier.getInstance().onDisable();

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
