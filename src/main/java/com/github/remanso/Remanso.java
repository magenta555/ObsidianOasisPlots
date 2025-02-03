package com.github.remanso;

// Import necessary classes from the Bukkit API for plugin development
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Material;
import java.util.List;
import java.util.ArrayList;

// Main class that extends JavaPlugin, the base class for all Bukkit plugins
public class Remanso extends JavaPlugin {
    
    // Called when the plugin is enabled
    public void onEnable() {
        // Save the default configuration file if it doesn't exist
        saveResource("config.yml", false);
        
        // Register event listeners for handling specific game events
        // Bukkit.getPluginManager().registerEvents(new Zone(this), this);
        
        // Set command executors for custom commands defined in plugin.yml
        this.getCommand("remanso").setExecutor(this);
        this.getCommand("r").setExecutor(this);
    }

    // Handle commands sent by players or console
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        // Check if no arguments were provided; display plugin version info
        if (a.length == 0) {
            s.sendMessage("§dRemanso, version 0, created by magenta555");
            s.sendMessage("§dLand zoning plugin for Minecraft servers!");
            return true; // Command processed successfully
        }
        
        // Reload the plugin's configuration if "reload" command is issued
        if (a[0].equals("reload")) {
            reloadConfig(); // Reloads the config.yml file from disk
            s.sendMessage("§dReloaded Remanso config.yml");
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
            
            s.sendMessage("§dYou should now have a Zoning Tool!"); // Confirm action to sender
        }
        
        return true; // Command processed successfully
    }

    // Provide tab completion options for commands
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        List<String> tabComplete = new ArrayList<String>();

        if (a.length == 0) {
            tabComplete.add("help");
            tabComplete.add("reload");
            tabComplete.add("teleport");
            tabComplete.add("teleport-location");
            tabComplete.add("tool");
            tabComplete.add("zone");
        }

        if (a.length == 1 && a[0].equals("teleport")) {
            tabComplete.add("RETURN LIST OF ZONES");
        }

        if (a.length == 1 && a[0].equals("zone")) {
            tabComplete.add("chunk");
            tabComplete.add("create");
            tabComplete.add("delete");
            tabComplete.add("designate");
            tabComplete.add("merge");
            tabComplete.add("world");
        }
        
        return tabComplete;
    }
}
