// Defines the package for the class
package com.github.remanso.listeners;

// Imports the Remanso plugin class
import com.github.remanso.Remanso;
// Imports the Zone model class
import com.github.remanso.model.Zone;
// Imports the Player class from Bukkit API
import org.bukkit.entity.Player;
// Imports the EventHandler annotation from Bukkit API
import org.bukkit.event.EventHandler;
// Imports the Listener interface from Bukkit API
import org.bukkit.event.Listener;
// Imports the BlockBreakEvent class from Bukkit API
import org.bukkit.event.block.BlockBreakEvent;
// Imports the BlockPlaceEvent class from Bukkit API
import org.bukkit.event.block.BlockPlaceEvent;
// Imports the PlayerInteractEvent class from Bukkit API
import org.bukkit.event.player.PlayerInteractEvent;
// Imports the Block class from Bukkit API
import org.bukkit.block.Block;
// Imports the Material class from Bukkit API
import org.bukkit.Material;
// Imports the Openable interface from Bukkit API
import org.bukkit.block.data.Openable;

// Defines the BlockInteractListener class, implementing Listener
public class BlockInteractListener implements Listener {

    // Declares a private final field to hold an instance of the Remanso plugin
    private final Remanso plugin;

    // Constructor for BlockInteractListener, taking a Remanso plugin instance as an argument
    public BlockInteractListener(Remanso plugin) {
        // Assigns the provided plugin instance to the plugin field
        this.plugin = plugin;
    }

    // Handles block break events
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Retrieves the player who broke the block
        Player player = event.getPlayer();
        // Retrieves the block that was broken
        Block block = event.getBlock();
        // Retrieves the zone that contains this block, if any
        Zone zone = plugin.getZones().values().stream()
                // Filters zones to find one containing the block's location
                .filter(z -> z.contains(block.getLocation()))
                // Finds the first matching zone or returns null if none found
                .findFirst()
                .orElse(null);

        // Checks if a zone was found at this block's location
        if (zone != null) {
            // Checks if player is not OP and does not own or is not allowed in this zone 
            if (!player.isOp() && !zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
                // Cancels the block break event 
                event.setCancelled(true);
                // Sends a message to the player indicating they are not allowed to break blocks in this zone 
                player.sendMessage("§cYou are not allowed to break blocks in this zone.");
            }
        }
    }

    // Handles block place events
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Retrieves the player who placed the block
        Player player = event.getPlayer();
        // Retrieves the block that was placed
        Block block = event.getBlock();
        // Retrieves the zone that contains this block, if any
        Zone zone = plugin.getZones().values().stream()
                // Filters zones to find one containing the block's location
                .filter(z -> z.contains(block.getLocation()))
                // Finds the first matching zone or returns null if none found
                .findFirst()
                .orElse(null);

        // Checks if a zone was found at this block's location
        if (zone != null) {
            // Checks if player is not OP and does not own or is not allowed in this zone 
            if (!player.isOp() && !zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
                // Cancels the block place event 
                event.setCancelled(true);
                // Sends a message to the player indicating they are not allowed to place blocks in this zone 
                player.sendMessage("§cYou are not allowed to place blocks in this zone.");
            }
        }
    }

    // Handles player interaction events with blocks
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Retrieves the player who interacted with a block 
        Player player = event.getPlayer();
        // Retrieves the block that was clicked 
        Block block = event.getClickedBlock();

        // Checks if no block was clicked 
        if (block == null) {
            return;  // Exit method if no block is clicked 
        }

        // Retrieves the zone that contains this block, if any 
        Zone zone = plugin.getZones().values().stream()
                // Filters zones to find one containing the block's location 
                .filter(z -> z.contains(block.getLocation()))
                // Finds the first matching zone or returns null if none found 
                .findFirst()
                .orElse(null);

        // Checks if a zone was found at this block's location 
        if (zone != null) {
            // Checks if player is not OP and does not own or is not allowed in this zone 
            if (!player.isOp() && !zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
                Material blockType = block.getType();  // Gets type of clicked block
                
                // Checks if clicked block is an openable type or specific types of containers and machines 
                if (block.getBlockData() instanceof Openable ||
                        blockType == Material.CHEST ||
                        blockType == Material.TRAPPED_CHEST ||
                        blockType.name().endsWith("_FURNACE") ||
                        blockType == Material.BREWING_STAND ||
                        blockType == Material.HOPPER ||
                        blockType == Material.DISPENSER ||
                        blockType == Material.DROPPER ||
                        blockType == Material.JUKEBOX ||
                        blockType == Material.NOTE_BLOCK ||
                        blockType == Material.LECTERN) {
                    // Cancels interaction with these blocks 
                    event.setCancelled(true);
                    // Sends a message to the player indicating they are not allowed to interact with this block in this zone 
                    player.sendMessage("§cYou are not allowed to interact with this block in this zone.");
                }
            }
        }
    }
}
