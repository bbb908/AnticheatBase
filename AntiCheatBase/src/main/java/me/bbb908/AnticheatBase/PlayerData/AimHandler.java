package me.bbb908.AnticheatBase.PlayerData;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class AimHandler {
    private ProfileClass profile;
    public AimHandler(ProfileClass prof) {
        profile = prof;
    }

    public float currentYaw = -9e9f;
    public float currentPitch = -9e9f;
    public float lastYaw = -9e9f;
    public float lastPitch = -9e9f;
    public float deltaYaw = -9e9f;
    public float deltaPitch = -9e9f;
    public float lastDeltaYaw = -9e9f;
    public float lastDeltaPitch = -9e9f;

    public void onPacket(PacketEvent event) {

        if (!isAimPacket(event.getPacketType())) return;
        PacketContainer packet = event.getPacket();

        lastYaw = currentYaw;
        lastPitch = currentPitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;

        if (currentYaw != -9e9f) {
            deltaYaw = Math.abs(packet.getFloat().read(0) - currentYaw);
            deltaPitch = Math.abs(packet.getFloat().read(1) - currentPitch);
        }

        currentYaw = packet.getFloat().read(0);
        currentPitch = packet.getFloat().read(1);

    }

    public boolean isAimPacket(PacketType packetType) {
        return (packetType == PacketType.Play.Client.LOOK || packetType == PacketType.Play.Client.POSITION_LOOK);
    }

}
