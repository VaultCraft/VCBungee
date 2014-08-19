package net.vaultcraft.vcbungee.database.mongo;

import net.vaultcraft.vcbungee.config.ClassConfig;

/**
 * Created by tacticalsk8er on 7/22/2014.
 */
public class MongoInfo {
    @ClassConfig.Config(path = "MongoDB.Host")
    public static String host = "localhost";
    @ClassConfig.Config(path = "MongoDB.Port")
    public static int port = 27017;
}
