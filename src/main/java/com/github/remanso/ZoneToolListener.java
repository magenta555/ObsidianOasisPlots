package com.github.remanso;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;

public class ZoneToolListener implements Listener {

    private final Remanso plugin;
    private final ZoneManager zoneManager;

    public ZoneToolListener(Remanso plugin, ZoneManager zoneManager) {
        this.plugin = plugin;
        this.zoneManager = zoneManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();

        if (item != null && isZoneTool(item)) {
            if (action == Action.LEFT_CLICK_BLOCK) {
                Location clickedBlockLocation = event.getClickedBlock().getLocation();
                zoneManager.setPosition1(player, clickedBlockLocation);
                event.setCancelled(true); // Prevent block breaking
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                Location clickedBlockLocation = event.getClickedBlock().getLocation();
                zoneManager.setPosition2(player, clickedBlockLocation);
                event.setCancelled(true); // Prevent block interaction
            }
        }
    }

    // Check if item is the zone tool based on name and lore (from config)
    private boolean isZoneTool(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        String displayName = plugin.getConfig().getString("remanso.tool.name");
        String lore = plugin.getConfig().getString("remanso.tool.lore");

        if (meta.hasDisplayName() && meta.getDisplayName().equals(displayName) && meta.hasLore() && meta.getLore().contains(lore)) {
            return true;
        }

        return false;
    }
}
