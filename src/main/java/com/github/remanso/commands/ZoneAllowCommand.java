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
// Imports the OfflinePlayer class from Bukkit API
import org.bukkit.OfflinePlayer;

// Defines the ZoneAllowCommand class, implementing CommandExecutor
public class ZoneAllowCommand implements CommandExecutor {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for ZoneAllowCommand, taking a Remanso plugin instance as an argument
    public ZoneAllowCommand(Remanso plugin) {
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
        // Checks if the player does not have the "remanso.zoneallow" permission
        if (!player.hasPermission("remanso.zoneallow")) {
            // Sends a message to the player indicating they do not have permission
            player.sendMessage("§dYou do not have permission to use this command.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Checks if the number of arguments is not equal to 1
        if (args.length != 1) {
            // Sends a usage message to the player
            player.sendMessage("Usage: /zoneallow <player>");
            // Returns true to indicate the command was handled
            return true;
        }

        // Retrieves the target player's name from the command arguments
        String targetPlayerName = args[0];
        // Retrieves the OfflinePlayer object using the target player's name
        OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(targetPlayerName);

        // Checks if the target player is null or has not played before
        if (targetPlayer == null || !targetPlayer.hasPlayedBefore()) {
            // Sends a message to the player indicating the target player was not found
            player.sendMessage("Player '" + targetPlayerName + "' not found.");
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
            player.sendMessage("§dYou are not standing in a zone.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Checks if the player is not the owner of the zone
        if (!zone.getOwner().equals(player.getUniqueId())) {
            // Sends a message to the player indicating they do not own the zone
            player.sendMessage("§dYou do not own this zone.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Checks if the target player is already allowed in the zone
        if (zone.getAllowedPlayers().contains(targetPlayer.getUniqueId().toString())) {
            // Sends a message to the player indicating the target player is already allowed
            player.sendMessage("Player '" + targetPlayerName + "' is already allowed in this zone.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Adds the target player's UUID to the list of allowed players for the zone
        zone.getAllowedPlayers().add(targetPlayer.getUniqueId().toString());
        // Saves the updated zone data to persistent storage
        plugin.saveZones();
        // Sends a message to the player indicating the target player is now allowed
        player.sendMessage("§aPlayer '" + targetPlayerName + "' is now allowed in this zone.");
        // Returns true to indicate the command was handled
        return true;
    }
}
