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

// Imports the List interface from Java's util package
import java.util.List;
// Imports the UUID class from Java's util package
import java.util.UUID;
// Imports the Collectors class for stream operations
import java.util.stream.Collectors;

// Defines the ZoneInfoCommand class, implementing CommandExecutor and TabCompleter
public class ZoneInfoCommand implements CommandExecutor, TabCompleter {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for ZoneInfoCommand, taking a Remanso plugin instance as an argument
    public ZoneInfoCommand(Remanso plugin) {
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
        // Checks if the player does not have the "remanso.zoneinfo" permission
        if (!player.hasPermission("remanso.zoneinfo")) {
            // Sends a message to the player indicating they do not have permission
            player.sendMessage("§dYou do not have permission to use this command.");
            // Returns true to indicate the command was handled
            return true;
        }

        Zone zone;
        // Checks if no arguments were provided
        if (args.length == 0) {
            // Retrieves the zone that contains the player's current location, if any
            zone = plugin.getZones().values().stream()
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
        } else {
            // Retrieves the zone name from command arguments
            String zoneName = args[0];
            // Retrieves the specified zone using its name 
            zone = plugin.getZones().get(zoneName);
            // Checks if no zone was found with that name 
            if (zone == null) {
                // Sends a message to the player indicating that zone was not found 
                player.sendMessage("§dZone '" + zoneName + "' not found.");
                // Returns true to indicate that command execution was handled successfully 
                return true;
            }
        }

        // Sends zone information to the player 
        player.sendMessage("§dZone Info:");
        player.sendMessage("§7Name: §f" + zone.getName());
        
        // Checks if there is an owner for this zone 
        if (zone.getOwner() != null) {
            // Sends a message with owner's name 
            player.sendMessage("§7Owner: §f" + plugin.getServer().getOfflinePlayer(zone.getOwner()).getName());
        } else {
            // Indicates that there is no owner for this zone 
            player.sendMessage("§7Owner: §fNone");
        }
        
        // Sends availability status of this zone 
        player.sendMessage("§7Available: §f" + zone.isAvailable());
        
        // Sends location details of this zone 
        player.sendMessage("§7Location: §f" + zone.getMinX() + ", " + zone.getMinY() + ", " + zone.getMinZ() + " to " + zone.getMaxX() + ", " + zone.getMaxY() + ", " + zone.getMaxZ());

        List<String> allowedPlayers = zone.getAllowedPlayers();
        
        // Checks if there are allowed players for this zone 
        if (!allowedPlayers.isEmpty()) {
            StringBuilder allowedPlayersString = new StringBuilder();
            allowedPlayersString.append("§7Allowed Players: §f");
            
            // Iterates through allowed players and constructs a string of their names 
            for (int i = 0; i < allowedPlayers.size(); i++) {
                UUID playerUUID = UUID.fromString(allowedPlayers.get(i));
                String playerName = plugin.getServer().getOfflinePlayer(playerUUID).getName();
                allowedPlayersString.append(playerName);
                
                // Adds a comma separator between names 
                if (i < allowedPlayers.size() - 1) {
                    allowedPlayersString.append(", ");
                }
            }
            
            // Sends list of allowed players to the player 
            player.sendMessage(allowedPlayersString.toString());
        } else {
            // Indicates that there are no allowed players for this zone 
            player.sendMessage("§7Allowed Players: §fNone");
        }

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
