package me.bbb908.AnticheatBase.Events;

import me.bbb908.AnticheatBase.Anticheat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {
    Anticheat profileHandler;

    public Join(Anticheat phandler) {
        profileHandler = phandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        profileHandler.addUser(event.getPlayer());
    }
}
