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

// Defines the ZoneClaimCommand class, implementing CommandExecutor
public class ZoneClaimCommand implements CommandExecutor {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for ZoneClaimCommand, taking a Remanso plugin instance as an argument
    public ZoneClaimCommand(Remanso plugin) {
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
        // Checks if the player does not have the "remanso.zoneclaim" permission
        if (!player.hasPermission("remanso.zoneclaim")) {
            // Sends a message to the player indicating they do not have permission
            player.sendMessage("You do not have permission to use this command.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Retrieves the zone the player is standing in, if any
        Zone zone = plugin.getZones().values().stream()
                // Filters zones to find one containing the player's location
                .filter(z -> z.contains(player.getLocation()))
                // Finds the first matching zone
                .findFirst()
                // Returns null if no zone is found
                .orElse(null);

        // Checks if the player is not standing in a zone
        if (zone == null) {
            // Sends a message to the player indicating they are not in a zone
            player.sendMessage("You are not standing in a zone.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Checks if the zone is not available for claiming
        if (!zone.isAvailable()) {
            // Sends a message to the player indicating that the zone is not available for claiming
            player.sendMessage("This zone is not available for claiming.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Sets the owner of the zone to be the player who executed the command
        zone.setOwner(player.getUniqueId());
        // Sets the zone's availability status to false, marking it as claimed
        zone.setAvailable(false); // Set the zone as no longer available
        
        // Saves the updated zone data to persistent storage
        plugin.saveZones();

        // Sends a message to the player indicating they have successfully claimed the zone
        player.sendMessage("§aYou have claimed this zone.");
        // Returns true to indicate the command was handled successfully
        return true;
    }
}
