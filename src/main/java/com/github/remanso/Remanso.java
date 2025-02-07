// src/main/java/com/github/remanso/Remanso.java
package com.github.remanso;

import com.github.remanso.commands.*;
import com.github.remanso.listeners.BlockInteractListener;
import com.github.remanso.model.Zone;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Remanso extends JavaPlugin {

    private final Map<String, Zone> zones = new HashMap<>();
    private FileConfiguration zonesConfig;

    @Override
    public void onEnable() {
        zonesConfig = getConfig();
        loadZones();

        getCommand("zonetool").setExecutor(new ZoneToolCommand());
        getCommand("zonecreate").setExecutor(new ZoneCreateCommand(this));
        getCommand("zoneavailable").setExecutor(new ZoneAvailableCommand(this));
        getCommand("zonedelete").setExecutor(new ZoneDeleteCommand(this));
        getCommand("zoneclaim").setExecutor(new ZoneClaimCommand(this));
        getCommand("zoneinfo").setExecutor(new ZoneInfoCommand(this));
        getCommand("zoneallow").setExecutor(new ZoneAllowCommand(this));
        getCommand("zoneteleport").setExecutor(new ZoneTeleportCommand(this));
        getCommand("zonesetteleport").setExecutor(new ZoneSetTeleportCommand(this));

        getServer().getPluginManager().registerEvents(new BlockInteractListener(this), this);

        getLogger().info("Remanso plugin enabled!");
    }

    @Override
    public void onDisable() {
        saveZones();
        getLogger().info("Remanso plugin disabled!");
    }

    public Map<String, Zone> getZones() {
        return zones;
    }

    public FileConfiguration getZonesConfig() {
        return zonesConfig;
    }

    public void loadZones() {
        ConfigurationSection zonesSection = zonesConfig.getConfigurationSection("zones");
        if (zonesSection != null) {
            for (String zoneName : zonesSection.getKeys(false)) {
                ConfigurationSection zoneSection = zonesSection.getConfigurationSection(zoneName);
                if (zoneSection != null) {
                    Zone zone = new Zone();
                    zone.setName(zoneName);
                    zone.setWorldName(zoneSection.getString("world"));
                    zone.setMinX(zoneSection.getInt("minX"));
                    zone.setMinY(zoneSection.getInt("minY"));
                    zone.setMinZ(zoneSection.getInt("minZ"));
                    zone.setMaxX(zoneSection.getInt("maxX"));
                    zone.setMaxY(zoneSection.getInt("maxY"));
                    zone.setMaxZ(zoneSection.getInt("maxZ"));
                    zone.setAvailable(zoneSection.getBoolean("available"));
                    String ownerId = zoneSection.getString("owner");
                    if (ownerId != null) {
                        zone.setOwner(UUID.fromString(ownerId));
                    }
                    zone.getAllowedPlayers().addAll(zoneSection.getStringList("allowedPlayers"));

                    double teleportX = zoneSection.getDouble("teleportX");
                    double teleportY = zoneSection.getDouble("teleportY");
                    double teleportZ = zoneSection.getDouble("teleportZ");
                    String teleportWorld = zoneSection.getString("teleportWorld");
                    if (teleportWorld != null) {
                        Location teleportLocation = new Location(getServer().getWorld(teleportWorld), teleportX, teleportY, teleportZ);
                        zone.setTeleportLocation(teleportLocation);
                    }
                    zones.put(zoneName, zone);
                }
            }
        }
    }

    public void saveZones() {
        zonesConfig.set("zones", null);
        for (Map.Entry<String, Zone> entry : zones.entrySet()) {
            String zoneName = entry.getKey();
            Zone zone = entry.getValue();
            String path = "zones." + zoneName + ".";
            zonesConfig.set(path + "world", zone.getWorldName());
            zonesConfig.set(path + "minX", zone.getMinX());
            zonesConfig.set(path + "minY", zone.getMinY());
            zonesConfig.set(path + "minZ", zone.getMinZ());
            zonesConfig.set(path + "maxX", zone.getMaxX());
            zonesConfig.set(path + "maxY", zone.getMaxY());
            zonesConfig.set(path + "maxZ", zone.getMaxZ());
            zonesConfig.set(path + "available", zone.isAvailable());
            if (zone.getOwner() != null) {
                zonesConfig.set(path + "owner", zone.getOwner().toString());
            }
            zonesConfig.set(path + "allowedPlayers", zone.getAllowedPlayers());
            if (zone.getTeleportLocation() != null) {
                zonesConfig.set(path + "teleportX", zone.getTeleportLocation().getX());
                zonesConfig.set(path + "teleportY", zone.getTeleportLocation().getY());
                zonesConfig.set(path + "teleportZ", zone.getTeleportLocation().getZ());
                zonesConfig.set(path + "teleportWorld", zone.getTeleportLocation().getWorld().getName());
            }
        }
        saveConfig();
    }
}
