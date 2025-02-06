package com.github.remanso;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class ZoneManager {

    private final Remanso plugin;
    public final Map<UUID, Zone> playerZones = new HashMap<>(); // Store zone by player UUID
    private final Map<UUID, Location> position1 = new HashMap<>();
    private final Map<UUID, Location> position2 = new HashMap<>();

    public ZoneManager(Remanso plugin) {
        this.plugin = plugin;
        loadZonesFromConfig(); // Load zones when the plugin starts
    }

    public void setPosition1(Player player, Location location) {
        position1.put(player.getUniqueId(), location);
        player.sendMessage("§aPosition 1 set!");
    }

    public void setPosition2(Player player, Location location) {
        position2.put(player.getUniqueId(), location);
        player.sendMessage("§aPosition 2 set!");
    }

    public boolean createZoneCommand(CommandSender sender, String zoneName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (!position1.containsKey(playerUUID) || !position2.containsKey(playerUUID)) {
            player.sendMessage("§cYou must set both positions first!");
            return true;
        }

        Location pos1 = position1.get(playerUUID);
        Location pos2 = position2.get(playerUUID);

        Zone newZone = new Zone(playerUUID, pos1, pos2, zoneName); // Pass zoneName to Zone constructor
        playerZones.put(playerUUID, newZone);

        saveZoneToConfig(playerUUID, newZone); // Saves the zone immediately
        player.sendMessage("§aZone '" + zoneName + "' created!");

        // Cleanup positions after creating zone
        position1.remove(playerUUID);
        position2.remove(playerUUID);

        return true;
    }

    // Saves a zone to the configuration file
    public void saveZoneToConfig(UUID playerUUID, Zone zone) {
        FileConfiguration config = plugin.getConfig();
        String path = "zones." + playerUUID.toString();

        config.set(path + ".zoneName", zone.getZoneName()); //Save the zone name

        config.set(path + ".world", zone.getPos1().getWorld().getName());
        config.set(path + ".pos1.x", zone.getPos1().getX());
        config.set(path + ".pos1.y", zone.getPos1().getY());
        config.set(path + ".pos1.z", zone.getPos1().getZ());

        config.set(path + ".pos2.x", zone.getPos2().getX());
        config.set(path + ".pos2.y", zone.getPos2().getY());
        config.set(path + ".pos2.z", zone.getPos2().getZ());

        plugin.saveConfig();
    }

    // Loads a zone from the configuration file
    public void loadZonesFromConfig() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection zonesSection = config.getConfigurationSection("zones");

        if (zonesSection != null) {
            for (String playerUUIDString : zonesSection.getKeys(false)) {
                try {
                    UUID playerUUID = UUID.fromString(playerUUIDString);
                    String path = "zones." + playerUUIDString;

                    String zoneName = config.getString(path + ".zoneName", "Unnamed Zone"); // Get the zone name, default to 'Unnamed Zone' if not found

                    // Get the world name from the configuration
                    String worldName = config.getString(path + ".world");
                    if (worldName == null) {
                        plugin.getLogger().warning("World name is missing for zone: " + playerUUIDString);
                        continue; // Skip to the next zone if the world name is missing
                    }
                    org.bukkit.World world = plugin.getServer().getWorld(worldName);
                    if (world == null) {
                        plugin.getLogger().warning("World not found: " + worldName + " for zone: " + playerUUIDString);
                        continue; // Skip to the next zone if the world is not found
                    }

                    // Retrieve positions from the configuration
                    double pos1X = config.getDouble(path + ".pos1.x");
                    double pos1Y = config.getDouble(path + ".pos1.y");
                    double pos1Z = config.getDouble(path + ".pos1.z");
                    Location pos1 = new Location(world, pos1X, pos1Y, pos1Z);

                    double pos2X = config.getDouble(path + ".pos2.x");
                    double pos2Y = config.getDouble(path + ".pos2.y");
                    double pos2Z = config.getDouble(path + ".pos2.z");
                    Location pos2 = new Location(world, pos2X, pos2Y, pos2Z);

                    // Create the zone and add it to the playerZones map
                    Zone zone = new Zone(playerUUID, pos1, pos2, zoneName);
                    playerZones.put(playerUUID, zone);
                    plugin.getLogger().info("Loaded zone for player: " + playerUUID);

                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID format in config: " + playerUUIDString);
                } catch (Exception e) {
                    plugin.getLogger().warning("Error loading zone for player: " + playerUUIDString + ": " + e.getMessage());
                }
            }
        } else {
            plugin.getLogger().info("No zones found in config to load.");
        }
    }

    // Saves all zones in playerZones to the configuration file
    public void saveZonesToConfig() {
        for (Map.Entry<UUID, Zone> entry : playerZones.entrySet()) {
            UUID playerUUID = entry.getKey();
            Zone zone = entry.getValue();
            saveZoneToConfig(playerUUID, zone);
        }
    }


    public Zone getZoneByPlayer(UUID playerUUID) {
        return playerZones.get(playerUUID);
    }

        // New method to get a list of zone names
    public List<String> getZoneNames() {
        List<String> zoneNames = new ArrayList<>();
        for (Map.Entry<UUID, Zone> entry : playerZones.entrySet()) {
            Zone zone = entry.getValue();
            zoneNames.add(zone.getZoneName()); // Get zone name from Zone object
        }
        return zoneNames;
    }

    public boolean isInsideZone(Location location, Zone zone) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double minX = Math.min(zone.getPos1().getX(), zone.getPos2().getX());
        double minY = Math.min(zone.getPos1().getY(), zone.getPos2().getY());
        double minZ = Math.min(zone.getPos1().getZ(), zone.getPos2().getZ());
        double maxX = Math.max(zone.getPos1().getX(), zone.getPos2().getX());
        double maxY = Math.max(zone.getPos1().getY(), zone.getPos2().getY());
        double maxZ = Math.max(zone.getPos1().getZ(), zone.getPos2().getZ());

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ && location.getWorld().equals(zone.getPos1().getWorld());
    }

    public Remanso getPlugin() {
        return plugin;
    }
}
