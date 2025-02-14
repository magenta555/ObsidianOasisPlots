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

// Imports the List interface from Java's util package
import java.util.List;
// Imports the Collectors class for stream operations
import java.util.stream.Collectors;

// Defines the ZoneListCommand class, implementing CommandExecutor
public class ZoneListCommand implements CommandExecutor {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for ZoneListCommand, taking a Remanso plugin instance as an argument
    public ZoneListCommand(Remanso plugin) {
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
        // Checks if the player does not have the "remanso.zonelist" permission
        if (!player.hasPermission("remanso.zonelist")) {
            // Sends a message to the player indicating they do not have permission
            player.sendMessage("You do not have permission to use this command.");
            // Returns true to indicate the command was handled
            return true;
        }

        List<Zone> zones;  // Declares a list to hold zones

        // Checks if the player is OP (operator)
        if (player.isOp()) {
            // If OP, retrieves all zones and collects them into a list 
            zones = plugin.getZones().values().stream().collect(Collectors.toList());
        } else {
            // If not OP, retrieves only zones owned by or allowed for this player 
            zones = plugin.getZones().values().stream()
                    .filter(zone -> (zone.getOwner() != null && zone.getOwner().equals(player.getUniqueId())) || zone.getAllowedPlayers().contains(player.getUniqueId().toString()))
                    .collect(Collectors.toList());
        }

        // Checks if no zones were found for this player 
        if (zones.isEmpty()) {
            // Sends a message indicating that there are no zones available to this player 
            player.sendMessage("§dYou are not an owner or allowed player in any zones.");
            // Returns true to indicate that command execution was handled successfully 
            return true;
        }

        // Sends a header message for zone listing 
        player.sendMessage("§dYour Zones:");
        
        // Iterates through each zone and sends its name to the player 
        zones.forEach(zone -> player.sendMessage("§d- §f" + zone.getName()));
        
        return true;  // Returns true to indicate that command execution was successful 
    }
}
