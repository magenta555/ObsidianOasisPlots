// src/main/java/com/github/remanso/listeners/BlockInteractListener.java
package com.github.remanso.listeners;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.data.Openable;

public class BlockInteractListener implements Listener {

    private final Remanso plugin;

    public BlockInteractListener(Remanso plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Zone zone = plugin.getZones().values().stream()
                .filter(z -> z.contains(block.getLocation()))
                .findFirst()
                .orElse(null);

        if (zone != null) {
            if (!player.isOp() && !zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
                event.setCancelled(true);
                player.sendMessage("§cYou are not allowed to break blocks in this zone.");
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Zone zone = plugin.getZones().values().stream()
                .filter(z -> z.contains(block.getLocation()))
                .findFirst()
                .orElse(null);

        if (zone != null) {
            if (!player.isOp() && !zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
                event.setCancelled(true);
                player.sendMessage("§cYou are not allowed to place blocks in this zone.");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        Zone zone = plugin.getZones().values().stream()
                .filter(z -> z.contains(block.getLocation()))
                .findFirst()
                .orElse(null);

        if (zone != null) {
            if (!player.isOp() && !zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
                Material blockType = block.getType();
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
                        blockType == Material.LECTERN)
                         {
                    event.setCancelled(true);
                    player.sendMessage("§cYou are not allowed to interact with this block in this zone.");
                }
            }
        }
    }
}
