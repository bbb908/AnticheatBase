package me.bbb908.AnticheatBase.Checks.impl;

import com.comphenix.protocol.events.PacketEvent;
import me.bbb908.AnticheatBase.Checks.Check;
import me.bbb908.AnticheatBase.PlayerData.MovementHandler;
import me.bbb908.AnticheatBase.PlayerData.ProfileClass;

public class ExampleCheck extends Check {

    public ExampleCheck(ProfileClass prof) {
        super(prof, "Speed", "Example");
    }

    @Override
    public void onPacket(PacketEvent event) {
        MovementHandler movementHandler = getProfile().getMovementHandler();

        if (movementHandler.deltaXZ > movementHandler.predictSpeed()) {
            if (increaseBuffer() >= 3) {
                flag("Speed: "+movementHandler.deltaXZ);
            }
        } else {
            decreaseBuffer(0.5);
        }
    }

    @Override
    public void onInitilise() {
        setMaxFlags(5);
    }
}
