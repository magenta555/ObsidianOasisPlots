package com.github.remanso;

// Import necessary classes from the Bukkit API for plugin development
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Material;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.bukkit.entity.HumanEntity;

// Main class that extends JavaPlugin, the base class for all Bukkit plugins
public class MinecraftGlass extends JavaPlugin {
    
    // Called when the plugin is enabled
    public void onEnable() {
        // Save the default configuration file if it doesn't exist
        saveResource("config.yml", false);
        
        // Register event listeners for handling specific game events
        Bukkit.getPluginManager().registerEvents(new Zone(this), this);
        
        // Set command executors for custom commands defined in plugin.yml
        this.getCommand("remanso").setExecutor(this);
        this.getCommand("r").setExecutor(this);
    }

    // Handle commands sent by players or console
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        // Check if no arguments were provided; display plugin version info
        if (a.length == 0) {
            s.sendMessage("Remanso, version 0, by magenta555");
            return true; // Command processed successfully
        }
        
        // Reload the plugin's configuration if "reload" command is issued
        if (a[0].equals("reload")) {
            reloadConfig(); // Reloads the config.yml file from disk
            s.sendMessage("Reloaded Remanso config.yml");
            return true; // Command processed successfully
        }
        
        // Handle the "tool" command to give a custom item to the player
        if (a[0].equals("tool")) {
            Player player = Bukkit.getServer().getPlayer(s.getName()); // Get the player who issued the command
            
            // Create an ItemStack using material defined in config.yml
            ItemStack itemStack = new ItemStack(Material.matchMaterial(getConfig().getString("remanso.tool.material")));
            
            // Get and set metadata for the item (display name and lore)
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(getConfig().getString("remanso.tool.name")); // Set custom display name
            
            List<String> loreList = new ArrayList<String>(); // Create a list for lore descriptions
            loreList.add(getConfig().getString("remanso.tool.lore")); // Add lore from config
            itemMeta.setLore(loreList); // Set lore on the item
            
            // Check if the item should have an enchantment glint and apply it if so
            if (getConfig().getString("remanso.tool.glint").equals("true")) {
                itemMeta.setEnchantmentGlintOverride(true); // Enable glint effect on item
            }
            
            itemStack.setItemMeta(itemMeta); // Apply metadata to the ItemStack
            
            player.getInventory().addItem(itemStack); // Add the customized item to player's inventory
            
            s.sendMessage("Added the item to the player's inventory!"); // Confirm action to sender
        }
        
        return true; // Command processed successfully
    }

    // Provide tab completion options for commands (currently not implemented)
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        return true; // Return true to indicate tab completion is available (placeholder)
    }
}
