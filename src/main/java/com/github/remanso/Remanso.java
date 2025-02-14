// Defines the package for the class
package com.github.remanso;

// Imports necessary classes from the commands, listeners, and model packages
import com.github.remanso.commands.*;
import com.github.remanso.listeners.*;
import com.github.remanso.model.*;
// Imports the JavaPlugin class from Bukkit API
import org.bukkit.plugin.java.JavaPlugin;
// Imports the FileConfiguration class from Bukkit API
import org.bukkit.configuration.file.FileConfiguration;
// Imports the ConfigurationSection class from Bukkit API
import org.bukkit.configuration.ConfigurationSection;
// Imports the Location class from Bukkit API
import org.bukkit.Location;

// Imports utility classes for collections
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Defines the Remanso class, extending JavaPlugin
public class Remanso extends JavaPlugin {

    // Declares a map to hold zones, using LinkedHashMap to preserve order
    private final Map<String, Zone> zones = new LinkedHashMap<>();
    // Declares a FileConfiguration to hold zone configurations
    private FileConfiguration zonesConfig;

    // Method called when the plugin is enabled
    @Override
    public void onEnable() {
        zonesConfig = getConfig();  // Loads the configuration file
        loadZones();  // Loads zones from configuration

        // Registers commands with their corresponding executors
        getCommand("zonetool").setExecutor(new ZoneToolCommand());
        getCommand("zonecreate").setExecutor(new ZoneCreateCommand(this));
        getCommand("zoneavailable").setExecutor(new ZoneAvailableCommand(this));
        getCommand("zonedelete").setExecutor(new ZoneDeleteCommand(this));
        getCommand("zoneclaim").setExecutor(new ZoneClaimCommand(this));
        getCommand("zoneinfo").setExecutor(new ZoneInfoCommand(this));
        getCommand("zoneallow").setExecutor(new ZoneAllowCommand(this));
        getCommand("zonedisallow").setExecutor(new ZoneDisallowCommand(this));
        getCommand("zonelist").setExecutor(new ZoneListCommand(this));
        getCommand("zoneteleport").setExecutor(new ZoneTeleportCommand(this));
        getCommand("zonesetteleport").setExecutor(new ZoneSetTeleportCommand(this));

        // Registers event listeners
        getServer().getPluginManager().registerEvents(new BlockInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new ZoneToolListener(), this);
        getServer().getPluginManager().registerEvents(new ZoneEnterListener(this), this);

        // Logs that the plugin has been enabled
        getLogger().info("Remanso plugin enabled!");
    }

    // Method called when the plugin is disabled
    @Override
    public void onDisable() {
        saveZones();  // Saves zones to configuration before disabling
        getLogger().info("Remanso plugin disabled!");  // Logs that the plugin has been disabled
    }

    // Getter for the zones map
    public Map<String, Zone> getZones() {
        return zones;
    }

    // Getter for the zones configuration file
    public FileConfiguration getZonesConfig() {
        return zonesConfig;
    }

    // Method to load zones from configuration file
    public void loadZones() {
        ConfigurationSection zonesSection = zonesConfig.getConfigurationSection("zones");  // Retrieves the zone section from config
        if (zonesSection != null) {
            List<String> zoneNames = new ArrayList<>(zonesSection.getKeys(false));  // Gets all zone names in the section
            Collections.reverse(zoneNames);  // Load in reverse order

            // Iterates through each zone name to load its data
            for (String zoneName : zoneNames) {
                ConfigurationSection zoneSection = zonesSection.getConfigurationSection(zoneName);  // Retrieves individual zone section
                if (zoneSection != null) {
                    Zone zone = new Zone();  // Creates a new Zone object
                    zone.setName(zoneName);  // Sets the name of the zone
                    zone.setWorldName(zoneSection.getString("world"));  // Sets the world name of the zone
                    zone.setMinX(zoneSection.getInt("minX"));  // Sets minimum X coordinate of the zone
                    zone.setMinY(zoneSection.getInt("minY"));  // Sets minimum Y coordinate of the zone
                    zone.setMinZ(zoneSection.getInt("minZ"));  // Sets minimum Z coordinate of the zone
                    zone.setMaxX(zoneSection.getInt("maxX"));  // Sets maximum X coordinate of the zone
                    zone.setMaxY(zoneSection.getInt("maxY"));  // Sets maximum Y coordinate of the zone
                    zone.setMaxZ(zoneSection.getInt("maxZ"));  // Sets maximum Z coordinate of the zone
                    zone.setAvailable(zoneSection.getBoolean("available"));  // Sets availability status of the zone
                    
                    String ownerId = zoneSection.getString("owner");  // Retrieves owner ID as string 
                    if (ownerId != null) {
                        zone.setOwner(UUID.fromString(ownerId));  // Sets owner UUID if available 
                    }
                    
                    zone.getAllowedPlayers().addAll(zoneSection.getStringList("allowedPlayers"));  // Adds allowed players to the list

                    double teleportX = zoneSection.getDouble("teleportX");  // Retrieves teleport X coordinate 
                    double teleportY = zoneSection.getDouble("teleportY");  // Retrieves teleport Y coordinate 
                    double teleportZ = zoneSection.getDouble("teleportZ");  // Retrieves teleport Z coordinate 
                    String teleportWorld = zoneSection.getString("teleportWorld");  // Retrieves teleport world name 
                    
                    if (teleportWorld != null) {  
                        Location teleportLocation = new Location(getServer().getWorld(teleportWorld), teleportX, teleportY, teleportZ);  // Creates teleport location object 
                        zone.setTeleportLocation(teleportLocation);  // Sets teleport location for this zone 
                    }
                    
                    zones.put(zoneName, zone);  // Adds loaded zone to the map 
                }
            }
        }
    }

    // Method to save zones to configuration file 
    public void saveZones() {
        zonesConfig.set("zones", null);  // Clear existing zones in config

        List<String> zoneNames = new ArrayList<>(zones.keySet());  // Create a list of current zone names 
        Collections.reverse(zoneNames);  // Reverse order for saving 

        for (String zoneName : zoneNames) {  
            Zone zone = zones.get(zoneName);  // Retrieve current zone by name 
            String path = "zones." + zoneName + ".";  // Base path for this specific zone in config
            
            zonesConfig.set(path + "world", zone.getWorldName());  // Save world name 
            zonesConfig.set(path + "minX", zone.getMinX());  // Save minimum X coordinate 
            zonesConfig.set(path + "minY", zone.getMinY());  // Save minimum Y coordinate 
            zonesConfig.set(path + "minZ", zone.getMinZ());  // Save minimum Z coordinate 
            zonesConfig.set(path + "maxX", zone.getMaxX());  // Save maximum X coordinate 
            zonesConfig.set(path + "maxY", zone.getMaxY());  // Save maximum Y coordinate 
            zonesConfig.set(path + "maxZ", zone.getMaxZ());  // Save maximum Z coordinate 
            zonesConfig.set(path + "available", zone.isAvailable());  // Save availability status 

            if (zone.getOwner() != null) {  
                zonesConfig.set(path + "owner", zone.getOwner().toString());  // Save owner UUID as string 
            }
            
            zonesConfig.set(path + "allowedPlayers", zone.getAllowedPlayers());  // Save list of allowed players 

            if (zone.getTeleportLocation() != null) {  
                zonesConfig.set(path + "teleportX", zone.getTeleportLocation().getX());  
                zonesConfig.set(path + "teleportY", zone.getTeleportLocation().getY());  
                zonesConfig.set(path + "teleportZ", zone.getTeleportLocation().getZ());  
                zonesConfig.set(path + "teleportWorld", zone.getTeleportLocation().getWorld().getName());  
            }
        }
        
        saveConfig();  // Saves changes to config file 
    }
}
