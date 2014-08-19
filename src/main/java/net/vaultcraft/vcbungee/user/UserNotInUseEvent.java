package net.vaultcraft.vcbungee.user;

import net.md_5.bungee.api.plugin.Event;

/**
 * Created by tacticalsk8er on 8/19/2014.
 */
public class UserNotInUseEvent extends Event {

    private NetworkUser user;

    public UserNotInUseEvent(NetworkUser user) {
        this.user = user;
    }

    public NetworkUser getUser() {
        return user;
    }
}
