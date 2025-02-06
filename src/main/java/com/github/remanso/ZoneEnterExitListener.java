package com.github.remanso;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZoneEnterExitListener implements Listener {

    private final Remanso plugin;
    private final ZoneManager zoneManager;
    private final Map<UUID, Boolean> playerInZone = new HashMap<>(); // Track if a player is in a zone

    public ZoneEnterExitListener(Remanso plugin, ZoneManager zoneManager) {
        this.plugin = plugin;
        this.zoneManager = zoneManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Location to = event.getTo();

        if (to == null) return; // Prevent null pointer exceptions

        for (Map.Entry<UUID, Zone> entry : zoneManager.playerZones.entrySet()) {
            UUID ownerUUID = entry.getKey();
            Zone zone = entry.getValue();
            boolean inside = zoneManager.isInsideZone(to, zone);

            //Check if player is the owner of the zone
            if (ownerUUID.equals(playerUUID)) {
                if (inside && (playerInZone.get(playerUUID) == null || !playerInZone.get(playerUUID))) {
                    player.sendMessage("§aYou have entered your zone: " + zone.getZoneName() + "!");
                    playerInZone.put(playerUUID, true);
                } else if (!inside && (playerInZone.get(playerUUID) != null && playerInZone.get(playerUUID))) {
                    player.sendMessage("§cYou have exited your zone: " + zone.getZoneName() + "!");
                    playerInZone.put(playerUUID, false);
                }
            } else {
                // Handle other players entering/exiting the zone
                Player owner = plugin.getServer().getPlayer(ownerUUID);
                if (owner != null) {
                    String zoneName = zone.getZoneName();
                    if (inside && (playerInZone.get(playerUUID) == null || !playerInZone.get(playerUUID))) {
                        player.sendMessage("§aYou have entered " + owner.getName() + "'s zone: " + zoneName + "!");
                        playerInZone.put(playerUUID, true);
                    } else if (!inside && (playerInZone.get(playerUUID) != null && playerInZone.get(playerUUID))) {
                        player.sendMessage("§cYou have exited " + owner.getName() + "'s zone: " + zoneName + "!");
                        playerInZone.put(playerUUID, false);
                    }
                }
            }
        }
    }
}
