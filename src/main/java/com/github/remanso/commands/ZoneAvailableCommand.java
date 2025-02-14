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

// Defines the ZoneAvailableCommand class, implementing CommandExecutor
public class ZoneAvailableCommand implements CommandExecutor {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for ZoneAvailableCommand, taking a Remanso plugin instance as an argument
    public ZoneAvailableCommand(Remanso plugin) {
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
        // Checks if the player does not have the "remanso.zoneavailable" permission
        if (!player.hasPermission("remanso.zoneavailable")) {
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

        // Checks if the player is not the owner of the zone
        if (!zone.getOwner().equals(player.getUniqueId())) {
            // Sends a message to the player indicating they do not own the zone
            player.sendMessage("You do not own this zone.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Clears the current owner of the zone by setting it to null
        zone.setOwner(null);
        // Clears all currently allowed players from the zone's list of allowed players
        zone.getAllowedPlayers().clear();
        // Sets the zone's availability status to true, making it available for claiming
        zone.setAvailable(true);
        // Saves the updated zone data to persistent storage
        plugin.saveZones();
        // Sends a message to the player indicating that the zone is now available for claiming
        player.sendMessage("§aThis zone is now available for claiming.");
        // Returns true to indicate the command was handled successfully
        return true;
    }
}
