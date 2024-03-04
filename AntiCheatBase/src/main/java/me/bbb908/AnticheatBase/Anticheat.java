package me.bbb908.AnticheatBase;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.bbb908.AnticheatBase.Events.Exemptions;
import me.bbb908.AnticheatBase.Events.Join;
import me.bbb908.AnticheatBase.Events.Leave;
import me.bbb908.AnticheatBase.PlayerData.ProfileClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Anticheat extends JavaPlugin {

    HashMap<Player, ProfileClass> Users = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        for (Player player : Bukkit.getOnlinePlayers()) {
            Users.putIfAbsent(player,new ProfileClass(player,this));
        }

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        PacketType[] listenFor = {
                PacketType.Play.Client.POSITION_LOOK,
                PacketType.Play.Client.POSITION,
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.USE_ENTITY,
                PacketType.Play.Client.USE_ITEM,
                PacketType.Play.Client.BOAT_MOVE,
                PacketType.Play.Client.VEHICLE_MOVE,
                PacketType.Play.Client.STEER_VEHICLE,
        };

        manager.addPacketListener(new PacketAdapter(this,listenFor) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Users.get(event.getPlayer()).onPacket(event);
            }
        });

        manager.addPacketListener(new PacketAdapter(this,PacketType.Play.Server.ENTITY_TELEPORT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                //Users.get(event.getPlayer()).onTeleport(event);
            }
        });

        manager.addPacketListener(new PacketAdapter(this,PacketType.Play.Server.ENTITY_VELOCITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Users.get(event.getPlayer()).onVelocity(event);
            }
        });

        Listener[] listeners = {
                new Join(this),
                new Leave(this),
                new Exemptions(this)
        };

        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener,this);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void addUser(Player player) {
        Users.put(player,new ProfileClass(player,this));
    }

    public void removeUsers(Player player) {
        Users.remove(player);
    }

    public ProfileClass getProfile(Player player) {
        return Users.get(player);
    }
}
