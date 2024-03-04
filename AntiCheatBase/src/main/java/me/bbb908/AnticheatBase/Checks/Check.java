package me.bbb908.AnticheatBase.Checks;

import com.comphenix.protocol.PacketType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import com.comphenix.protocol.events.PacketEvent;
import me.bbb908.AnticheatBase.PlayerData.ProfileClass;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class Check {
    ProfileClass profile;
    String checkName;
    String checkType;
    double buffer = 0;
    int flags = 0;
    int maxFlags = 10; //default max
    boolean setbacksEnabled = true;

    public Check(ProfileClass prof,String name,String Type) {
        profile = prof;
        checkName = name;
        checkType = Type;
    }

    public String getCheckName() {
        return checkName;
    }

    public String getCheckType() {
        return checkType;
    }

    public ProfileClass getProfile() {
        return profile;
    }

    /*
    public void flag_Player(Player player,String extras) {

        if (extras.isEmpty()) extras = "No Extra Data";

        // Create a new TextComponent
        TextComponent message = new TextComponent("§3AntiRGB §0» §3"+player.getName()+" §fflagged §3"+checkName+" §f(§3"+checkType+"§f)");

        int x = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getX());
        int y = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getY());
        int z = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getZ());

        // Create a new HoverEvent
        String[] hoverMessage = {
                "§3Ping: §f"+ player.getPing(),
                "§3Verbose: §f"+extras,
                "§3Location: §fx:"+x+" y:"+y+" z:"+z
        };

        String hoverText = String.join("\n", hoverMessage);

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText));

        // Set the hover event to the message
        message.setHoverEvent(hoverEvent);

        // Broadcast the message
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("ac.viewalerts")) {
                onlinePlayer.spigot().sendMessage(message);
            }
        }

    }

    public void flag_velo(String extras,String veloType) {

        if (extras.isEmpty()) extras = "No Extra Data";

        // Create a new TextComponent
        TextComponent message = new TextComponent("§3AC §0» §3"+profile.getPlayer().getName()+" §fflagged §3"+"Velocity"+" §f(§3"+veloType+"§f)");

        int x = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getX());
        int y = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getY());
        int z = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getZ());

        // Create a new HoverEvent
        String[] hoverMessage = {
                "§3Ping: §f"+ profile.getPlayer().getPing(),
                "§3Verbose: §f"+extras,
                "§3Location: §fx:"+x+" y:"+y+" z:"+z
        };

        String hoverText = String.join("\n", hoverMessage);

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText));

        // Set the hover event to the message
        message.setHoverEvent(hoverEvent);

        // Broadcast the message
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("ac.viewalerts")) {
                onlinePlayer.spigot().sendMessage(message);
            }
        }

    }*/

    public void setMaxFlags(int i) {
        maxFlags = i;
    }

    public void flag(String extras) {

        profile.getSetbackUtils().flagged();

        flags++;

        if (extras.isEmpty()) extras = "No Extra Data";

        // Create a new TextComponent
        TextComponent message = new TextComponent("§4AC §0» §3"+profile.getPlayer().getName()+" §fflagged §3"+checkName+" §f(§3"+checkType+"§f) §0[§f"+flags+"§7/§f"+maxFlags+"§0]");

        int x = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getX());
        int y = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getY());
        int z = (int) Math.floor(getProfile().getMovementHandler().currentLocation.getZ());

        // Create a new HoverEvent
        String[] hoverMessage = {
                "§3 *** Player ***",
                "§3Ping: §f"+ profile.getPlayer().getPing(),
                "§3Location: §fx:"+x+" y:"+y+" z:"+z,
                "",
                "§3 *** Check ***",
                "§3Verbose: §f"+extras,
                "§3Buffer: §f"+buffer
        };

        String hoverText = String.join("\n", hoverMessage);

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText));

        // Set the hover event to the message
        message.setHoverEvent(hoverEvent);

        if (flags >= maxFlags) {
            getProfile().getPlayer().sendMessage("§cYou would be kicked now!");
            flags = 0;
        }

        // Broadcast the message
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("ac.viewalerts")) {
                onlinePlayer.spigot().sendMessage(message);
            }
        }

    }

    boolean queued = false;

    public void setback() {
        if (queued) return;
        if (!setbacksEnabled) return;
        if (System.currentTimeMillis() - profile.joinTime < 2000) return;

        profile.getSetbackUtils().setback();
    }

    public double increaseBuffer() {
        return buffer++;
    }

    public void decreaseBuffer(double amount) {
        if (buffer > 0) buffer -= amount;
    }

    public double getBufferAmount() {
        return buffer;
    }

    public void resetBuffer() {
        buffer = 0;
    }

    public void debug(String message) {
        profile.getPlayer().sendMessage("§f[§cDEBUG§f]:§7 "+message);
    }

    public boolean isVehiclePacket(PacketType packetType) {
        return (packetType.name().equals("VEHICLE_MOVE"));
    }

    public boolean isMovePacket(PacketType packetType) {
        return (packetType == PacketType.Play.Client.POSITION || packetType == PacketType.Play.Client.POSITION_LOOK);
    }

    public boolean isAimPacket(PacketType packetType) {
        return (packetType == PacketType.Play.Client.POSITION_LOOK || packetType == PacketType.Play.Client.LOOK);
    }

    public abstract void onPacket(PacketEvent event);
    public abstract void onInitilise();
}