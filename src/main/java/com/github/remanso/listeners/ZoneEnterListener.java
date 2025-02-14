// Defines the package for the class
package com.github.remanso.listeners;

// Imports the Remanso plugin class
import com.github.remanso.Remanso;
// Imports the Zone model class
import com.github.remanso.model.Zone;
// Imports the ChatColor class from Bukkit API
import org.bukkit.ChatColor;
// Imports the Location class from Bukkit API
import org.bukkit.Location;
// Imports the Player class from Bukkit API
import org.bukkit.entity.Player;
// Imports the EventHandler annotation from Bukkit API
import org.bukkit.event.EventHandler;
// Imports the Listener interface from Bukkit API
import org.bukkit.event.Listener;
// Imports the PlayerMoveEvent class from Bukkit API
import org.bukkit.event.player.PlayerMoveEvent;
// Imports the PlayerTeleportEvent class from Bukkit API
import org.bukkit.event.player.PlayerTeleportEvent;

// Imports the HashMap and Map classes from Java's util package
import java.util.HashMap;
import java.util.Map;
// Imports the UUID class from Java's util package
import java.util.UUID;

// Defines the ZoneEnterListener class, implementing Listener
public class ZoneEnterListener implements Listener {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;
    // Declares a map to track players' last zones by their UUIDs
    private final Map<UUID, String> playerLastZone = new HashMap<>();

    // Constructor for ZoneEnterListener, taking a Remanso plugin instance as an argument
    public ZoneEnterListener(Remanso plugin) {
        // Assigns the provided plugin instance to the plugin field
        this.plugin = plugin;
    }

    // Handles player movement events
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Calls handleZoneChange method to check for zone changes on player movement
        handleZoneChange(event.getPlayer(), event.getFrom(), event.getTo());
    }

    // Handles player teleportation events
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Calls handleZoneChange method to check for zone changes on player teleportation
        handleZoneChange(event.getPlayer(), event.getFrom(), event.getTo());
    }

    // Method to handle zone changes based on player's movement or teleportation
    @SuppressWarnings("deprecation")
    private void handleZoneChange(Player player, Location from, Location to) {
        String enteredZoneName = null;  // To store name of zone entered by player
        String exitedZoneName = null;   // To store name of zone exited by player
        UUID playerId = player.getUniqueId();  // Retrieves player's unique ID

        Zone enteredZone = null;  // To store zone entered by player
        Zone exitedZone = null;   // To store zone exited by player

        // Check if player entered a new zone
        for (Zone zone : plugin.getZones().values()) {
            if (zone.contains(to)) {
                enteredZone = zone;  // Set entered zone if player's new location is within this zone
                enteredZoneName = zone.getName();  // Get name of entered zone
                break;  // Exit loop after finding the entered zone 
            }
        }

        // Check if player exited a zone 
        for (Zone zone : plugin.getZones().values()) {
            if (zone.contains(from)) {
                exitedZone = zone;  // Set exited zone if player's previous location was within this zone 
                exitedZoneName = zone.getName();  // Get name of exited zone 
                break;  // Exit loop after finding the exited zone 
            }
        }

        String lastZone = playerLastZone.get(playerId);  // Retrieves last known zone for this player

        // Checks if player has exited a zone and updates their last known zone accordingly 
        if (exitedZone != null && (lastZone == null || !lastZone.equals(exitedZoneName))) {
            playerLastZone.put(playerId, null);  // Clears last known zone since they have exited 
        } else if (exitedZone == null && lastZone != null) {
            // Player exited all zones 
            boolean stillInZone = false;  // Flag to check if still in any zones
            
            for (Zone zone : plugin.getZones().values()) {
                if (zone.contains(to)) {  // Checks if player's new location is within any zones 
                    stillInZone = true;  // Sets flag to true if still in a zone 
                    break;  // Exit loop since we found that they are still in a zone 
                }
            }
            
            if (!stillInZone) {  // If player is not in any zones 
                player.sendMessage(ChatColor.DARK_PURPLE + "You are no longer in a zone!");  // Sends notification message 
                playerLastZone.put(playerId, null);  // Clears last known zone 
            }
        }

        // Checks if player has entered a new zone and sends notification message 
        if (enteredZone != null && (lastZone == null || !lastZone.equals(enteredZoneName))) {
             player.sendMessage(ChatColor.LIGHT_PURPLE + "You are now entering zone: " + enteredZoneName);  // Sends notification message about entering a new zone 
             playerLastZone.put(playerId, enteredZoneName);  // Updates last known zone for this player 
        }

        // If neither entered nor exited zones, checks current location again 
        if(enteredZone == null && exitedZone == null){
            for (Zone zone : plugin.getZones().values()) {
                if(zone.contains(to)){  // Checks if player's current location is within any zones 
                    if (lastZone == null || !lastZone.equals(zone.getName())){  // If current location is in a different zone than before 
                        playerLastZone.put(playerId, zone.getName());  // Updates last known zone for this player 
                    }
                    break;  // Exit loop after finding matching zone 
                }
            }
        }
    }
}
