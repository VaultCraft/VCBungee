/*
 * Copyright (C) 2011 Vex Software LLC
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

package net.vaultcraft.vcbungee.vote.model.listeners;

import common.network.PacketOutVote;
import net.md_5.bungee.api.ProxyServer;
import net.vaultcraft.vcbungee.network.MessageServer;
import net.vaultcraft.vcbungee.vote.model.Vote;
import net.vaultcraft.vcbungee.vote.model.VoteListener;

/**
 * A basic vote listener for demonstration purposes.
 * 
 * @author Blake Beaupain
 */
public class BasicVoteListener implements VoteListener {
	public void voteMade(Vote vote) {
        ProxyServer.getInstance().getLogger().info("Vote Made.");
        MessageServer.sendPacketToAll(null, new PacketOutVote(vote.getUsername()));
	}
}
