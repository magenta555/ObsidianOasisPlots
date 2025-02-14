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
// Imports the TabCompleter interface from Bukkit API
import org.bukkit.command.TabCompleter;
// Imports the Player class from Bukkit API
import org.bukkit.entity.Player;
// Imports the Location class from Bukkit API
import org.bukkit.Location;

// Imports the List interface from Java's util package
import java.util.List;
// Imports the Collectors class for stream operations
import java.util.stream.Collectors;

// Defines the ZoneTeleportCommand class, implementing CommandExecutor and TabCompleter
public class ZoneTeleportCommand implements CommandExecutor, TabCompleter {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for ZoneTeleportCommand, taking a Remanso plugin instance as an argument
    public ZoneTeleportCommand(Remanso plugin) {
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
        // Checks if the player does not have the "remanso.zoneteleport" permission
        if (!player.hasPermission("remanso.zoneteleport")) {
            // Sends a message to the player indicating they do not have permission
            player.sendMessage("§dYou do not have permission to use this command.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Checks if exactly one argument is provided 
        if (args.length != 1) {
            // Sends a usage message to the player 
            player.sendMessage("Usage: /zoneteleport <name>");
            // Returns true to indicate that command execution was handled successfully 
            return true;
        }

        // Retrieves the zone name from command arguments 
        String zoneName = args[0];
        // Retrieves the specified zone using its name 
        Zone zone = plugin.getZones().get(zoneName);

        // Checks if no zone was found with that name 
        if (zone == null) {
            // Sends a message to the player indicating that zone was not found 
            player.sendMessage("Zone '" + zoneName + "' not found.");
            // Returns true to indicate that command execution was handled successfully 
            return true;
        }

        // Checks if player is neither OP nor owner nor allowed in this zone 
        if (!player.isOp() && !zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
            // Sends a message indicating that player is not allowed to teleport to this zone 
            player.sendMessage("§dYou are not allowed to teleport to this zone.");
            // Returns true to indicate that command execution was handled successfully 
            return true;
        }

        // Retrieves teleport location for this zone 
        Location teleportLocation = zone.getTeleportLocation();
        
        // If teleport location is null, sets it based on zone's world and coordinates 
        if (teleportLocation == null) {
            teleportLocation = new Location(plugin.getServer().getWorld(zone.getWorldName()), zone.getMinX(), zone.getMinY(), zone.getMinZ());
        }

        // Teleports the player to the specified location 
        player.teleport(teleportLocation);
        
        // Sends a message indicating successful teleportation 
        player.sendMessage("§aTeleported to zone '" + zoneName + "'.");
        
        return true;  // Returns true to indicate that command execution was successful 
    }

    // Overrides onTabComplete method from TabCompleter interface for command auto-completion 
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Checks if sender is not an instance of Player 
        if (!(sender instanceof Player)) {
            return null;  // Returns null for non-players 
        }

        Player player = (Player) sender;  // Casts sender to Player 
        
        // Checks if there is one argument provided for auto-completion 
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();  // Converts argument to lower case
            
            // Checks if player is OP for additional access 
            if (player.isOp()) {
                return plugin.getZones().values().stream()
                        .filter(zone -> zone.getName().toLowerCase().startsWith(partialName))  // Filters zones based on input prefix  
                        .map(Zone::getName)  // Maps zones to their names  
                        .collect(Collectors.toList());  // Collects results into a list  
                        
            } else {  // For non-OP players, restricts access based on ownership or permissions  
                return plugin.getZones().values().stream()
                        .filter(zone -> (zone.getOwner() != null && zone.getOwner().equals(player.getUniqueId())) || zone.getAllowedPlayers().contains(player.getUniqueId().toString()))  // Filters zones based on ownership or permissions  
                        .filter(zone -> zone.getName().toLowerCase().startsWith(partialName))  // Filters zones based on input prefix  
                        .map(Zone::getName)  // Maps zones to their names  
                        .collect(Collectors.toList());  // Collects results into a list  
            }
        }
        
        return null;  // Returns null when no conditions are met for auto-completion  
    }
}
