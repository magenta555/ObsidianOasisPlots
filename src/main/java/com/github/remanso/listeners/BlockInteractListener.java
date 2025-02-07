// src/main/java/com/github/remanso/listeners/BlockInteractListener.java
package com.github.remanso.listeners;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;

public class BlockInteractListener implements Listener {

    private final Remanso plugin;

    public BlockInteractListener(Remanso plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Zone zone = plugin.getZones().values().stream()
                .filter(z -> z.contains(event.getBlock().getLocation()))
                .findFirst()
                .orElse(null);

        if (zone != null) {
            if (!zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
                event.setCancelled(true);
                player.sendMessage("§cYou are not allowed to break blocks in this zone.");
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Zone zone = plugin.getZones().values().stream()
                .filter(z -> z.contains(event.getBlock().getLocation()))
                .findFirst()
                .orElse(null);

        if (zone != null) {
            if (!zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
                event.setCancelled(true);
                player.sendMessage("§cYou are not allowed to place blocks in this zone.");
            }
        }
    }
}
