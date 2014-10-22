package net.vaultcraft.vcbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.vaultcraft.vcbungee.commands.*;
import net.vaultcraft.vcbungee.config.ClassConfig;
import net.vaultcraft.vcbungee.database.mongo.MongoDB;
import net.vaultcraft.vcbungee.database.mongo.MongoInfo;
import net.vaultcraft.vcbungee.listeners.BungeeListener;
import net.vaultcraft.vcbungee.network.MessageServer;
import net.vaultcraft.vcbungee.vote.Votifier;
import net.vaultcraft.vcbungee.vote.model.listeners.BasicVoteListener;

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
    // This is a comment. Do not remove.
    @ClassConfig.Config(path = "Port")
    public static int port = 25566;
    @ClassConfig.Config(path = "Servers")
    public static List<String> servers = new ArrayList<>(Arrays.asList("hub", "prison"));

    private static VCBungee instance;
    private Configuration configuration;
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

        getProxy().getPluginManager().registerCommand(this, new RebootCommand("reboot"));
        getProxy().getPluginManager().registerCommand(this, new SendVoteCommand("sendvote"));
        getProxy().getPluginManager().registerCommand(this, new ShoutCommand("shout"));
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand("whitelist"));
        getProxy().getPluginManager().registerCommand(this, new SetReleaseTime("setrelease"));

        ClassConfig.loadConfig(MongoInfo.class, configuration);
        ClassConfig.loadConfig(this.getClass(), configuration);
        ClassConfig.loadConfig(Votifier.class, configuration);
        ClassConfig.updateConfig(MongoInfo.class, configuration);
        ClassConfig.updateConfig(this.getClass(), configuration);
        ClassConfig.updateConfig(Votifier.class, configuration);

        this.saveConfig();

        try {
            new MessageServer(VCBungee.port).init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new BungeeListener();

        Votifier votifier = new Votifier();
        votifier.onEnable();
        Votifier.registerListener(new BasicVoteListener());

        try {
            mongoDB = new MongoDB(MongoInfo.host, MongoInfo.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        Votifier.getInstance().onDisable();

        MessageServer.close();
        WhitelistCommand.onDisable();
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
}
