package me.bbb908.AnticheatBase.Events;

import me.bbb908.AnticheatBase.Anticheat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Leave implements Listener {
    Anticheat profileHandler;

    public Leave(Anticheat phandler) {
        profileHandler = phandler;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        profileHandler.removeUsers(event.getPlayer());
    }
}
