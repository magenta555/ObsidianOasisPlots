// Defines the package for the class
package com.github.remanso.commands;

// Imports the Command class from Bukkit API
import org.bukkit.command.Command;
// Imports the CommandExecutor interface from Bukkit API
import org.bukkit.command.CommandExecutor;
// Imports the CommandSender interface from Bukkit API
import org.bukkit.command.CommandSender;
// Imports the Player class from Bukkit API
import org.bukkit.entity.Player;
// Imports the Material class from Bukkit API
import org.bukkit.Material;
// Imports the ItemStack class from Bukkit API
import org.bukkit.inventory.ItemStack;
// Imports the ItemMeta class from Bukkit API
import org.bukkit.inventory.meta.ItemMeta;

// Imports Collections utility class from Java's util package
import java.util.Collections;

// Defines the ZoneToolCommand class, implementing CommandExecutor
public class ZoneToolCommand implements CommandExecutor {

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
        // Checks if the player does not have the "remanso.zonetool" permission
        if (!player.hasPermission("remanso.zonetool")) {
            // Sends a message to the player indicating they do not have permission
            player.sendMessage("§dYou do not have permission to use this command.");
            // Returns true to indicate the command was handled
            return true;
        }

        // Creates a new ItemStack representing a stick for the zoning tool
        ItemStack zoneTool = new ItemStack(Material.STICK);
        // Retrieves and modifies the item's metadata for display purposes
        ItemMeta meta = zoneTool.getItemMeta();
        meta.setDisplayName("§bZoning Tool");  // Sets the display name of the item
        meta.setLore(Collections.singletonList("§7Used to select corners for zones."));  // Sets a lore description for the item
        zoneTool.setItemMeta(meta);  // Applies the modified metadata back to the item

        // Adds the zoning tool to the player's inventory
        player.getInventory().addItem(zoneTool);
        // Sends a message to the player indicating they have received the zoning tool
        player.sendMessage("§aYou have been given a Zoning Tool.");
        
        return true;  // Returns true to indicate that command execution was successful 
    }
}
