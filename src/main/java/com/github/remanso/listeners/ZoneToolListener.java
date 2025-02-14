// Defines the package for the class
package com.github.remanso.listeners;

// Imports the LocationTagType class from the commands package
import com.github.remanso.commands.LocationTagType;
// Imports the ChatColor class from Bukkit API
import org.bukkit.ChatColor;
// Imports the Location class from Bukkit API
import org.bukkit.Location;
// Imports the Material class from Bukkit API
import org.bukkit.Material;
// Imports the Player class from Bukkit API
import org.bukkit.entity.Player;
// Imports the EventHandler annotation from Bukkit API
import org.bukkit.event.EventHandler;
// Imports the Listener interface from Bukkit API
import org.bukkit.event.Listener;
// Imports the Action enum from Bukkit API
import org.bukkit.event.block.Action;
// Imports the PlayerInteractEvent class from Bukkit API
import org.bukkit.event.player.PlayerInteractEvent;
// Imports the ItemStack class from Bukkit API
import org.bukkit.inventory.ItemStack;
// Imports the ItemMeta class from Bukkit API
import org.bukkit.inventory.meta.ItemMeta;
// Imports the PersistentDataContainer interface from Bukkit API
import org.bukkit.persistence.PersistentDataContainer;

// Defines the ZoneToolListener class, implementing Listener
public class ZoneToolListener implements Listener {

    // Handles player interaction events
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Retrieves the player who interacted
        Player player = event.getPlayer();
        // Retrieves the item in the player's hand
        ItemStack item = event.getItem();

        // Checks if the item is null or is not a stick
        if (item == null || item.getType() != Material.STICK) {
            return;  // Exit the method if not a stick
        }

        // Retrieves the item's metadata
        ItemMeta meta = item.getItemMeta();
        // Checks if the metadata is null or if the item does not have a display name, or if the stripped color of the display name is not "Zoning Tool"
        if (meta == null || !meta.hasDisplayName() || !ChatColor.stripColor(meta.getDisplayName()).equals("Zoning Tool")) {
            return;  // Exit the method if not the zoning tool
        }

        // Retrieves the action performed by the player
        Action action = event.getAction();
        // Retrieves the location of the clicked block, if any
        Location clickedBlockLocation = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null;

        // Checks if no block was clicked
        if (clickedBlockLocation == null) {
            return;  // Exit the method if no block was clicked
        }

        // Retrieves the player's persistent data container
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        // If the action was a left click on a block
        if (action == Action.LEFT_CLICK_BLOCK) {
            // Cancels the event to prevent default behavior
            event.setCancelled(true);
            // Sets the first position in the player's persistent data container to the clicked block's location
            dataContainer.set(LocationTagType.POS1_KEY, LocationTagType.TAG, clickedBlockLocation);
            // Sends a message to the player indicating that position 1 has been set
            player.sendMessage(ChatColor.GREEN + "Position 1 set to: " + clickedBlockLocation.getBlockX() + ", " + clickedBlockLocation.getBlockY() + ", " + clickedBlockLocation.getBlockZ());
        // If the action was a right click on a block
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            // Cancels the event to prevent default behavior
            event.setCancelled(true);
            // Sets the second position in the player's persistent data container to the clicked block's location
            dataContainer.set(LocationTagType.POS2_KEY, LocationTagType.TAG, clickedBlockLocation);
            // Sends a message to the player indicating that position 2 has been set
            player.sendMessage(ChatColor.GREEN + "Position 2 set to: " + clickedBlockLocation.getBlockX() + ", " + clickedBlockLocation.getBlockY() + ", " + clickedBlockLocation.getBlockZ());
        }
    }
}
