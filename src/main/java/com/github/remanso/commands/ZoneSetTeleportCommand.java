// Defines the package for the class
package com.github.remanso.commands;

// Imports the Remanso plugin class
import com.github.remanso.Remanso;
// Imports the Zone model class
import com.github.remanso.model.Zone;
// Imports the Command class from Bukkit API
import org.bukkit.command.Command;
// Imports the CommandExecutor interface from Bukkit API
import org.bukkit.command.CommandExecutor;
// Imports the CommandSender interface from Bukkit API
import org.bukkit.command.CommandSender;
// Imports the Player class from Bukkit API
import org.bukkit.entity.Player;
// Imports the Location class from Bukkit API
import org.bukkit.Location;

// Defines the ZoneSetTeleportCommand class, implementing CommandExecutor
public class ZoneSetTeleportCommand implements CommandExecutor {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for ZoneSetTeleportCommand, taking a Remanso plugin instance as an argument
    public ZoneSetTeleportCommand(Remanso plugin) {
        // Assigns the provided plugin instance to the plugin field
        this.plugin = plugin;
    }

    // Overrides the onCommand method from the CommandExecutor interface
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Checks if the sender is not an instance of Player
        if (!(sender instanceof Player)) {
            // Sends a message to the sender indicating the command can only be used by players
            sender.sendMessage("This command can only be used by players.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Casts the sender to a Player instance
        Player player = (Player) sender;
        // Checks if the player does not have the "remanso.zonesetteleport" permission
        if (!player.hasPermission("remanso.zonesetteleport")) {
            // Sends a message to the player indicating they do not have permission
            player.sendMessage("§dYou do not have permission to use this command.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Retrieves the zone that contains the player's current location, if any
        Zone zone = plugin.getZones().values().stream()
                // Filters zones to find one containing the player's location
                .filter(z -> z.contains(player.getLocation()))
                // Finds the first matching zone or returns null if none found
                .findFirst()
                .orElse(null);

        // Checks if no zone was found at the player's location
        if (zone == null) {
            // Sends a message to the player indicating they are not standing in a zone
            player.sendMessage("§dYou are not standing in a zone.");
            // Returns true to indicate that command execution was handled successfully 
            return true;
        }

        // Checks if the player does not own this zone
        if (!zone.getOwner().equals(player.getUniqueId())) {
            // Sends a message to the player indicating they do not own this zone
            player.sendMessage("§dYou do not own this zone.");
            // Returns true to indicate that command execution was handled successfully 
            return true;
        }

        // Retrieves the player's current location 
        Location playerLocation = player.getLocation();
        // Sets the teleport location for this zone to the player's current location 
        zone.setTeleportLocation(playerLocation);
        
        // Saves updated zones data to persistent storage after modification 
        plugin.saveZones();

        // Sends a message to the player indicating that teleport location has been set successfully 
        player.sendMessage("§aTeleport location for this zone has been set.");
        
        return true;  // Returns true to indicate that command execution was successful 
    }
}
