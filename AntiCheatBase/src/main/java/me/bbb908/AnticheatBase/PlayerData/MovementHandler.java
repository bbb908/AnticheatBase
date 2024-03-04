package me.bbb908.AnticheatBase.PlayerData;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.bbb908.AnticheatBase.Anticheat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;

public class MovementHandler {

    public double deltaXZ = -9e9;
    public double lastDeltaXZ = -9e9;
    public Location lastLoc;
    public double YMotion = -9e9;
    public Location lastGroundPos;
    public double lastYMotion = -9e9;
    private ProfileClass profile;
    public Location currentLocation;
    public long lastTeleport = System.currentTimeMillis() -10000;
    public double iceInfluence = 0;
    public long lastDamaged = System.currentTimeMillis() -10000;
    public int damageCompLength = 0;
    public boolean exemptHor = true;
    double lastPotionSpeedModifier = 0;
    public double damageCompAmount = 0;
    public long lastElytraUse = System.currentTimeMillis() -1000;
    public long lastSlimeInfluence = System.currentTimeMillis() -10000;
    public boolean onGround;
    public long lastVelocity = System.currentTimeMillis() -10000;
    public Anticheat plugin;
    Location lastSetbackLoc;
    long lastSetback = System.currentTimeMillis() -10000;
    public MovementHandler(ProfileClass prof) {
        profile = prof;
    }

    public void onVelo(PacketEvent event) {}

    public void setPlugin(Anticheat p) {
        plugin = p;
    }

    public void onTeleport(PacketEvent event) {
        lastTeleport = System.currentTimeMillis();
        PacketContainer packet = event.getPacket();

        double x = packet.getDoubles().read(0);
        double y = packet.getDoubles().read(1);
        double z = packet.getDoubles().read(2);

        Location builtLocation = new Location(event.getPlayer().getWorld(), x, y, z);

        currentLocation = builtLocation;
        lastLoc = builtLocation;
    }

    public void onPacket(PacketEvent event) {
        if (!isMovePacket(event.getPacketType()) && !isVehiclePacket(event.getPacketType())) return;
        PacketContainer packet = event.getPacket();

        if (currentLocation != null) {
            lastLoc = currentLocation;
        }

        if (profile.getPlayer().isGliding()) lastElytraUse = System.currentTimeMillis();

        lastYMotion = YMotion;

        double x = packet.getDoubles().read(0);
        double y = packet.getDoubles().read(1);
        double z = packet.getDoubles().read(2);
        onGround = packet.getBooleans().read(0);

        Location builtLocation = new Location(profile.getPlayer().getWorld(), x, y, z);
        currentLocation = builtLocation;

        // Get deltaXZ

        if (deltaXZ != -9e9) {
            lastDeltaXZ = deltaXZ;
        }

        Material[] iceBlocks = {
                Material.ICE,
                Material.BLUE_ICE,
                Material.PACKED_ICE,
                Material.FROSTED_ICE
        };

        if (isNearBlockList(iceBlocks)) {
            iceInfluence++;

            if (iceInfluence >= 3.5) iceInfluence = 3.5;
        } else if (iceInfluence > 0) {
            iceInfluence -= 0.25;

            if (iceInfluence < 0) iceInfluence = 0;
        }

        if (isNearBlock(Material.SLIME_BLOCK)) {
            double fallVelocity = 0;

            if (System.currentTimeMillis() - lastDamaged < damageCompLength) return;

            if (lastYMotion < 0) {
                fallVelocity = Math.abs(lastYMotion);
            }

            double roundedFall = Math.round(fallVelocity);

            damageCompLength = (int) (roundedFall * 1000);
            if (damageCompAmount == 0) damageCompLength = 200;

            damageCompAmount = (fallVelocity * 1.2) + ((double) damageCompLength / 100);
            lastDamaged = System.currentTimeMillis();
            exemptHor = false;
            lastSlimeInfluence = System.currentTimeMillis();
        } else {
            lastSlimeInfluence -= 50;
        }

        if (getBlockUnderPlayer().getType() != Material.AIR) lastGroundPos = currentLocation;

        if (lastLoc != null) {
            deltaXZ = removeLocationY(currentLocation).distance(removeLocationY(lastLoc));
            YMotion = currentLocation.getY() - lastLoc.getY();
        }
    }

    public double getDamageCompLW() {

        if (System.currentTimeMillis() - lastDamaged >= damageCompLength) return 0;

        return damageCompAmount;

    }

    public boolean getElytraComp() {

        if (System.currentTimeMillis() - lastElytraUse < 500) return true;

        return false;

    }

    public Block getBlockUnderPlayer() {
        return profile.getPlayer().getLocation().subtract(0,1,0).getBlock();
    }

    public void onVehicleExit() {
        damageCompLength = 50;
        damageCompAmount = 1;
        lastDamaged = System.currentTimeMillis();
    }

    public void onDamage(EntityDamageEvent event) {

        lastDamaged = System.currentTimeMillis();
        exemptHor = true;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            damageCompLength = 100;
            damageCompAmount = 0.05;
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            damageCompLength = 1000;
            damageCompAmount = 1;
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            damageCompLength = 100;
            damageCompAmount = 0.05;
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            damageCompLength = 500;
            damageCompAmount = 0.1;
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FREEZE) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) return;

        damageCompLength = 250;
        damageCompAmount = 0.2;

    }

    public boolean isMovePacket(PacketType packetType) {
        return (packetType == PacketType.Play.Client.POSITION || packetType == PacketType.Play.Client.POSITION_LOOK);
    }

    public Location removeLocationY(Location location) {
        return new Location(location.getWorld(),location.getX(),0,location.getZ());
    }

    public double predictSpeed() {
        // Boost
        double predictedSpeed = 0.432;

        if (profile.getPlayer().isFlying()) {
            predictedSpeed += 10;
        }

        boolean applied = false;

        for (PotionEffect potionEffect : profile.getPlayer().getActivePotionEffects()) {
            if (potionEffect.getType().getName().equals("SPEED")) {
                predictedSpeed += potionEffect.getAmplifier() *0.65;
                lastPotionSpeedModifier = potionEffect.getAmplifier() *0.65;
                applied = true;
            }
        }

        if (!applied && lastPotionSpeedModifier > 0.1) {
            predictedSpeed += lastPotionSpeedModifier;
            applied = false;
            lastPotionSpeedModifier /= 1.2;
        } else if (lastPotionSpeedModifier > 0 && !applied) lastPotionSpeedModifier = 0;

        if (isNearStairs()) {
            if (YMotion >= 0.5) {
                predictedSpeed += 2;
            }
        }

        if (System.currentTimeMillis() - lastElytraUse < 1000) predictedSpeed += 0.6;

        if (isBlockAbovePlayer()) {
            predictedSpeed += 0.08;
        }

        if (iceInfluence > 0) {
            predictedSpeed+= iceInfluence /11.2;
        }

        Material[] slowDownBlocks = {
                Material.COBWEB,
        };

        Material[] mediumSlowBlocks = {
                Material.POWDER_SNOW
        };

        Material[] lessSlowBlocks = {
                Material.SOUL_SAND,
        };

        if (isInBlocks(slowDownBlocks)) {
            predictedSpeed *= 0.35;
        }

        if (isOnBlocks(lessSlowBlocks)) predictedSpeed *= 0.6;

        if (isInBlocks(mediumSlowBlocks)) {
            predictedSpeed *= 0.5;
        }

        if (profile.getPlayer().isSprinting()) predictedSpeed += 0.01;

        if (System.currentTimeMillis() - lastSlimeInfluence < 750) {
            predictedSpeed += 0.2;
        }

        predictedSpeed += exemptHor ? getDamageCompLW() : 0;

        return predictedSpeed;
    }

    public boolean isInBlocks(Material[] mats) {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX();
        int startY = loc.getBlockY();
        int startZ = loc.getBlockZ();

        //for (int x = startX; x <= startX + 1; x++) {
            for (int y = startY; y <= startY + 1; y++) {
               // for (int z = startZ; z <= startZ + 2; z++) {
                    Block block = player.getWorld().getBlockAt(startX, y, startZ);
                    Material material = block.getType();

                    for (Material mat : mats) {
                        if (material == mat) {
                            return true;
                        }
                    }

               // }
           // }
        }

        return false;

    }

    public boolean isVehiclePacket(PacketType packetType) {
        return (packetType.name().equals("VEHICLE_MOVE"));
    }

    public boolean isOnBlocks(Material[] mats) {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX();
        int startY = loc.getBlockY();
        int startZ = loc.getBlockZ();

        //for (int x = startX; x <= startX + 1; x++) {
        for (int y = startY; y <= startY + 1; y++) {
            // for (int z = startZ; z <= startZ + 2; z++) {
            Block block = player.getWorld().getBlockAt(startX, y, startZ);
            Material material = block.getType();

            for (Material mat : mats) {
                if (material == mat) {
                    return true;
                }
            }

            // }
            // }
        }

        return false;

    }

    public boolean isNearBlockList(Material[] mats) {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX() -1;
        int startY = loc.getBlockY() -1;
        int startZ = loc.getBlockZ() -1;

        for (int x = startX; x <= startX + 2; x++) {
            for (int y = startY; y <= startY + 2; y++) {
                for (int z = startZ; z <= startZ + 2; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    Material material = block.getType();

                    for (Material mat : mats) {
                        if (material == mat) {
                            return true;
                        }
                    }

                }
            }
        }

        return false;
    }

    public boolean isNearBlock(Material mat) {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX() -1;
        int startY = loc.getBlockY() -1;
        int startZ = loc.getBlockZ() -1;

        for (int x = startX; x <= startX + 2; x++) {
            for (int y = startY; y <= startY + 2; y++) {
                for (int z = startZ; z <= startZ + 2; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    Material material = block.getType();

                    if (material == mat) {
                        return true;
                    }

                }
            }
        }

        return false;
    }

    public boolean isNearBed() {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX() -1;
        int startY = loc.getBlockY() -1;
        int startZ = loc.getBlockZ() -1;

        for (int x = startX; x <= startX + 2; x++) {
            for (int y = startY; y <= startY + 2; y++) {
                for (int z = startZ; z <= startZ + 2; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);

                    String typeName = block.getType().name();

                    if (typeName.contains("BED")) {
                        return true;
                    }
                }
            }
        }

        return false;


    }

    public boolean isNearStairs() {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX() -1;
        int startY = loc.getBlockY() -1;
        int startZ = loc.getBlockZ() -1;

        for (int x = startX; x <= startX + 2; x++) {
            for (int y = startY; y <= startY + 2; y++) {
                for (int z = startZ; z <= startZ + 2; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);

                    String typeName = block.getType().name();

                    if (typeName.contains("STAIRS")) {
                        return true;
                    }
                }
            }
        }

        return false;


    }

    public boolean isOnGround() {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX() -1;
        int startY = loc.getBlockY() -1;
        int startZ = loc.getBlockZ() -1;

        for (int x = startX; x <= startX + 2; x++) {
            for (int y = startY; y <= startY + 2; y++) {
                for (int z = startZ; z <= startZ + 2; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    Material material = block.getType();

                    if (material != Material.AIR) {
                        return true;
                    }

                }
            }
        }

        return false;
    }

    public boolean strict_isOnGround() {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX() -1;
        int startY = loc.getBlockY() -1;
        int startZ = loc.getBlockZ() -1;

        for (int x = startX; x <= startX + 2; x++) {
                for (int z = startZ; z <= startZ + 2; z++) {
                    Block block = player.getWorld().getBlockAt(x, startY, z);
                    Material material = block.getType();

                    if (material != Material.AIR) {
                        return true;
                    }

                }
        }

        return false;
    }

    @Deprecated
    public Location getSetbackPos() {
        if (System.currentTimeMillis() - lastSetback < 1000) {
            lastSetback = System.currentTimeMillis();
            return lastSetbackLoc;
        }

        lastSetbackLoc = lastLoc;
        lastSetback = System.currentTimeMillis();

        return lastLoc;
    }

    public boolean isBlockAbovePlayer() {
        Player player = profile.getPlayer();

        Location loc = player.getLocation();
        int startX = loc.getBlockX() -1;
        int startY = loc.getBlockY() +2;
        int startZ = loc.getBlockZ() -1;

        for (int x = startX; x <= startX + 2; x++) {
                for (int z = startZ; z <= startZ + 2; z++) {
                    Block block = player.getWorld().getBlockAt(x, startY, z);
                    Material material = block.getType();

                    if (material != Material.AIR) {
                        return true;
                    }

                }
        }

        return false;
    }

}
