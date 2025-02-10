package com.github.remanso.listeners;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZoneEnterListener implements Listener {

    private final Remanso plugin;
    private final Map<UUID, String> playerLastZone = new HashMap<>();

    public ZoneEnterListener(Remanso plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        handleZoneChange(event.getPlayer(), event.getFrom(), event.getTo());
    }

   @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        handleZoneChange(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @SuppressWarnings("deprecation")
    private void handleZoneChange(Player player, Location from, Location to) {
        String enteredZoneName = null;
        String exitedZoneName = null;
        UUID playerId = player.getUniqueId();

        Zone enteredZone = null;
        Zone exitedZone = null;

        // Check if player entered a new zone
        for (Zone zone : plugin.getZones().values()) {
            if (zone.contains(to)) {
                enteredZone = zone;
                enteredZoneName = zone.getName();
                break;
            }
        }

        // Check if player exited a zone
        for (Zone zone : plugin.getZones().values()) {
            if (zone.contains(from)) {
                exitedZone = zone;
                exitedZoneName = zone.getName();
                break;
            }
        }

        String lastZone = playerLastZone.get(playerId);

        if (exitedZone != null && (lastZone == null || !lastZone.equals(exitedZoneName))) {
            playerLastZone.put(playerId, null);
        } else if (exitedZone == null && lastZone != null) {
            // Player exited all zones
            boolean stillInZone = false;
            for (Zone zone : plugin.getZones().values()) {
                if (zone.contains(to)) {
                    stillInZone = true;
                    break;
                }
            }
            if (!stillInZone) {
                player.sendMessage(ChatColor.DARK_PURPLE + "You are no longer in a zone!");
                playerLastZone.put(playerId, null);
            }
        }

        if (enteredZone != null && (lastZone == null || !lastZone.equals(enteredZoneName))) {
             player.sendMessage(ChatColor.LIGHT_PURPLE + "You are now entering zone: " + enteredZoneName);
             playerLastZone.put(playerId, enteredZoneName);
        }

        if(enteredZone == null && exitedZone == null){
            for (Zone zone : plugin.getZones().values()) {
                if(zone.contains(to)){
                    if (lastZone == null || !lastZone.equals(zone.getName())){
                        playerLastZone.put(playerId, zone.getName());
                    }
                    break;
                }
            }
        }
    }
}
