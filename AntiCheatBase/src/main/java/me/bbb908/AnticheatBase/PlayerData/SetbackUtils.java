package me.bbb908.AnticheatBase.PlayerData;

import me.bbb908.AnticheatBase.Anticheat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class SetbackUtils {
    boolean setbacksEnabled = false;
    private final ProfileClass profile;
    private final MovementHandler movementHandler;

    public SetbackUtils(ProfileClass profile1) {
        profile = profile1;
        movementHandler = profile.getMovementHandler();
    }

    Location firstSetbackLoc;
    Location lastSetbackPos;
    long lastFlag = System.currentTimeMillis() -200;
    long lastSetback = System.currentTimeMillis() -100;

    public void flagged() {
        if (System.currentTimeMillis() - lastFlag >= 300) {
            firstSetbackLoc = movementHandler.lastLoc;
        }

        if (movementHandler.getBlockUnderPlayer().getType() == Material.AIR) {
            if (movementHandler.lastGroundPos == null) return;

            firstSetbackLoc = movementHandler.lastGroundPos;
        }

        lastFlag = System.currentTimeMillis();
    }

    public void setback() {
        if (!setbacksEnabled) return;
        if (System.currentTimeMillis() - lastSetback < 20) return;
        if (firstSetbackLoc == null) firstSetbackLoc = movementHandler.lastLoc;

        Bukkit.getScheduler().runTask(Anticheat.getPlugin(Anticheat.class), new Runnable() {
            @Override
            public void run() {
                if (lastSetbackPos != null) {
                    if (lastSetbackPos == firstSetbackLoc) {
                        return;
                    }
                }

                profile.getPlayer().teleport(firstSetbackLoc);
            }
        });

        lastSetback = System.currentTimeMillis();

    }

}
