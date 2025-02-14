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

// Imports the LinkedHashMap class from Java's util package
import java.util.LinkedHashMap;

// Imports the Location class from Bukkit API
import org.bukkit.Location;
// Imports the World class from Bukkit API
import org.bukkit.World;

// Defines the ZoneCreateCommand class, implementing CommandExecutor
public class ZoneCreateCommand implements CommandExecutor {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for ZoneCreateCommand, taking a Remanso plugin instance as an argument
    public ZoneCreateCommand(Remanso plugin) {
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
        // Checks if the player does not have the "remanso.zonecreate" permission
        if (!player.hasPermission("remanso.zonecreate")) {
            // Sends a message to the player indicating they do not have permission
            player.sendMessage("§dYou do not have permission to use this command.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Checks if the number of arguments is not equal to 1
        if (args.length != 1) {
            // Sends a usage message to the player
            player.sendMessage("Usage: /zonecreate <name>");
            // Returns true to indicate the command was handled
            return true;
        }

        // Retrieves the zone name from the command arguments
        String zoneName = args[0];
        // Checks if a zone with the given name already exists
        if (plugin.getZones().containsKey(zoneName)) {
            // Sends a message to the player indicating that a zone with that name already exists
            player.sendMessage("A zone with that name already exists.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Retrieves the first position from the player's persistent data container
        Location pos1 = player.getPersistentDataContainer().get(LocationTagType.POS1_KEY, LocationTagType.TAG);
        // Retrieves the second position from the player's persistent data container
        Location pos2 = player.getPersistentDataContainer().get(LocationTagType.POS2_KEY, LocationTagType.TAG);

        // Checks if either position is null
        if (pos1 == null || pos2 == null) {
            // Sends a message to the player indicating that they must select two corners first
            player.sendMessage("§dYou must select two corners with the Zoning Tool first.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Checks if the two positions are not in the same world
        if (!pos1.getWorld().equals(pos2.getWorld())) {
            // Sends a message to the player indicating that the positions must be in the same world
            player.sendMessage("The two positions must be in the same world.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Creates a new Zone object
        Zone zone = new Zone();
        // Sets the name of the zone
        zone.setName(zoneName);
        // Sets the world name of the zone
        zone.setWorldName(pos1.getWorld().getName());
        // Sets the minimum X coordinate of the zone
        zone.setMinX(Math.min(pos1.getBlockX(), pos2.getBlockX()));
        // Sets the minimum Z coordinate of the zone
        zone.setMinZ(Math.min(pos1.getBlockZ(), pos2.getBlockZ()));
        // Sets the maximum X coordinate of the zone
        zone.setMaxX(Math.max(pos1.getBlockX(), pos2.getBlockX()));
        // Sets the maximum Z coordinate of the zone
        zone.setMaxZ(Math.max(pos1.getBlockZ(), pos2.getBlockZ()));

        // Gets the world from position 1
        World world = pos1.getWorld();
        // Sets the minimum Y coordinate of the zone to the world's minimum height
        zone.setMinY(world.getMinHeight());
        // Sets the maximum Y coordinate of the zone to the world's maximum height
        zone.setMaxY(world.getMaxHeight());

        // Sets the owner of the zone to the player's unique ID
        zone.setOwner(player.getUniqueId());
        // Sets the zone as not available
        zone.setAvailable(false);

        // Set default teleport location to pos1
        zone.setTeleportLocation(pos1);

        // Create a temporary LinkedHashMap to hold the zones
        LinkedHashMap<String, Zone> tempZones = new LinkedHashMap<>();
        // Add the new zone to the beginning of the temporary map
        tempZones.put(zoneName, zone);
        // Put all the existing zones into the temporary map after the new zone
        tempZones.putAll(plugin.getZones());
        // Clear the original zones map
        plugin.getZones().clear();
        // Put all the zones from the temporary map back into the original zones map, preserving order
        plugin.getZones().putAll(tempZones);

        // Saves the updated zone data to persistent storage
        plugin.saveZones();

        // Sends a message to the player indicating that the zone was created successfully
        player.sendMessage("§aZone '" + zoneName + "' created successfully.");
        // Returns true to indicate the command was handled
        return true;
    }
}
