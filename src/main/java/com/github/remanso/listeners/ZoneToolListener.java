// src/main/java/com/github/remanso/listeners/ZoneToolListener.java
package com.github.remanso.listeners;

import com.github.remanso.commands.LocationTagType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class ZoneToolListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.STICK) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !ChatColor.stripColor(meta.getDisplayName()).equals("Zoning Tool")) {
            return;
        }

        Action action = event.getAction();
        Location clickedBlockLocation = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null;

        if (clickedBlockLocation == null) {
            return;
        }

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        if (action == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            dataContainer.set(LocationTagType.POS1_KEY, LocationTagType.TAG, clickedBlockLocation);
            player.sendMessage(ChatColor.GREEN + "Position 1 set to: " + clickedBlockLocation.getBlockX() + ", " + clickedBlockLocation.getBlockY() + ", " + clickedBlockLocation.getBlockZ());
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            dataContainer.set(LocationTagType.POS2_KEY, LocationTagType.TAG, clickedBlockLocation);
            player.sendMessage(ChatColor.GREEN + "Position 2 set to: " + clickedBlockLocation.getBlockX() + ", " + clickedBlockLocation.getBlockY() + ", " + clickedBlockLocation.getBlockZ());
        }
    }
}
