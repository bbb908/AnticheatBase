package me.bbb908.AnticheatBase.PlayerData;

import com.comphenix.protocol.events.PacketEvent;
import me.bbb908.AnticheatBase.Anticheat;
import me.bbb908.AnticheatBase.Checks.Check;
import me.bbb908.AnticheatBase.Checks.impl.ExampleCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ProfileClass {

    MovementHandler movementHandler = new MovementHandler(this);
    AimHandler aimHandler = new AimHandler(this);
    SetbackUtils setbackUtils = new SetbackUtils(this);
    private final Player player;
    public long joinTime = System.currentTimeMillis();

    Listener[] bukkit_checks = {

    };

    public ProfileClass(Player plr, Anticheat pl) {
        player = plr;

        for (Listener listener : bukkit_checks) {
            pl.getServer().getPluginManager().registerEvents(listener,pl);
        }

        movementHandler.setPlugin(pl);

        for (Check check : checks) {
            check.onInitilise();
        }
    }

    Check[] checks = {
            new ExampleCheck(this)
    };

    public void onPacket(PacketEvent event) {
        movementHandler.onPacket(event);
        aimHandler.onPacket(event);

        if (System.currentTimeMillis() - movementHandler.lastTeleport > 50) {
            for (Check check : checks) {
                check.onPacket(event);
            }
        }

    }

    public void onTeleport(PacketEvent event) {
        movementHandler.onTeleport(event);
    }

    public void onVelocity(PacketEvent event) {
        movementHandler.onVelo(event);
    }

    public Player getPlayer() {
        return player;
    }

    public MovementHandler getMovementHandler() {
        return movementHandler;
    }

    public SetbackUtils getSetbackUtils() {return setbackUtils;}

    public AimHandler getAimHandler() {
        return aimHandler;
    }
}