package me.bbb908.AnticheatBase.Events;

import me.bbb908.AnticheatBase.Anticheat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class Exemptions implements Listener {
    private final Anticheat plugin;

    public Exemptions(Anticheat p) {
        plugin = p;
    }

    @EventHandler
    public void entityDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        plugin.getProfile(player).getMovementHandler().onDamage(event);
    }

    @EventHandler
    public void dismountedVehicle(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) return;

        Player player = (Player) event.getExited();

        plugin.getProfile(player).getMovementHandler().onVehicleExit();
    }

    @EventHandler
    public void enterVehicle(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;

        Player player = (Player) event.getEntered();

        plugin.getProfile(player).getMovementHandler().onVehicleExit();
    }

    @EventHandler
    public void enterBed(PlayerBedEnterEvent e) {
        plugin.getProfile(e.getPlayer()).getMovementHandler().onVehicleExit();
    }

    @EventHandler
    public void leaveBed(PlayerBedLeaveEvent e) {
        plugin.getProfile(e.getPlayer()).getMovementHandler().onVehicleExit();
    }

}
