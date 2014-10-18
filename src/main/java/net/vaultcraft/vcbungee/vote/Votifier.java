/*
 * Copyright (C) 2012 Vex Software LLC
 * This file is part of Votifier.
 * 
 * Votifier is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Votifier is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Votifier.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.vaultcraft.vcbungee.vote;

import net.md_5.bungee.api.ProxyServer;
import net.vaultcraft.vcbungee.VCBungee;
import net.vaultcraft.vcbungee.config.ClassConfig;
import net.vaultcraft.vcbungee.vote.crypto.RSAIO;
import net.vaultcraft.vcbungee.vote.crypto.RSAKeygen;
import net.vaultcraft.vcbungee.vote.model.VoteListener;
import net.vaultcraft.vcbungee.vote.net.VoteReceiver;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * The main Votifier plugin class.
 *
 * @author Blake Beaupain
 */
public class Votifier {

    /**
     * The Votifier instance.
     */
    private static Votifier instance;

    /**
     * The vote listeners.
     */
    private final List<VoteListener> listeners = new ArrayList<>();

    /**
     * The vote receiver.
     */
    private VoteReceiver voteReceiver;

    /**
     * The RSA key pair.
     */
    private KeyPair keyPair;

    @ClassConfig.Config(path = "votes.host")
    public static String _hostAddress = "localhost";
    @ClassConfig.Config(path = "votes.port")
    public static int _hostPort = 8192;
    @ClassConfig.Config(path = "votes.debug")
    public static boolean _debug = false;
    @ClassConfig.Config(path = "votes.listener_folder")
    public static String _listenerDirectory;

    public void onEnable() {
        Votifier.instance = this;

        File rsaDirectory = new File(VCBungee.getInstance().getDataFolder() + "/rsa");
        // Replace to remove a bug with Windows paths - SmilingDevil
        if (_listenerDirectory == null)
            _listenerDirectory = VCBungee.getInstance().getDataFolder().toString()
                    .replace("\\", "/") + "/listeners";

		/*
         * Create RSA directory and keys if it does not exist; otherwise, read
		 * keys.
		 */
        try {
            if (!rsaDirectory.exists()) {
                rsaDirectory.mkdir();
                new File(_listenerDirectory).mkdir();
                keyPair = RSAKeygen.generate(2048);
                RSAIO.save(rsaDirectory, keyPair);
            } else {
                keyPair = RSAIO.load(rsaDirectory);
            }
        } catch (Exception ex) {
            gracefulExit();
            return;
        }

        if (_debug)
            System.out.println("DEBUG mode enabled!");

        ProxyServer.getInstance().getScheduler().runAsync(VCBungee.getInstance(), () -> {
            try {
                voteReceiver = new VoteReceiver(Votifier.this, _hostAddress, _hostPort);
                voteReceiver.start();
                System.out.println("Voting enabled!");
            } catch (Exception e) {
                gracefulExit();
            }
        });
    }

    public void onDisable() {
        // Interrupt the vote receiver.
        if (voteReceiver != null) {
            voteReceiver.shutdown();
        }
        System.out.println("Votes disabled!");
    }

    private void gracefulExit() {
        System.out.println("Could not load voting properly!");
    }

    /**
     * Gets the instance.
     *
     * @return The instance
     */
    public static Votifier getInstance() {
        return instance;
    }

    /**
     * Gets the listeners.
     *
     * @return The listeners
     */
    public List<VoteListener> getListeners() {
        return listeners;
    }

    /**
     * Gets the vote receiver.
     *
     * @return The vote receiver
     */
    public VoteReceiver getVoteReceiver() {
        return voteReceiver;
    }

    /**
     * Gets the keyPair.
     *
     * @return The keyPair
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }

    public boolean isDebug() {
        return _debug;
    }

    public String getVersion() {
        return "1.9";
    }

    public static void registerListener(VoteListener listener) {
        getInstance().listeners.add(listener);
    }
}