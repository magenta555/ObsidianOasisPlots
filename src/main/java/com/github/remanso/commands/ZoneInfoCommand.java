// src/main/java/com/github/remanso/commands/ZoneInfoCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ZoneInfoCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneInfoCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zoneinfo")) {
            player.sendMessage("§dYou do not have permission to use this command.");
            return true;
        }

        Zone zone = plugin.getZones().values().stream()
                .filter(z -> z.contains(player.getLocation()))
                .findFirst()
                .orElse(null);

        if (zone == null) {
            player.sendMessage("§dYou are not standing in a zone.");
            return true;
        }

        player.sendMessage("§dZone Info:");
        player.sendMessage("§7Name: §f" + zone.getName());
        if (zone.getOwner() != null) {
            player.sendMessage("§7Owner: §f" + plugin.getServer().getOfflinePlayer(zone.getOwner()).getName());
        } else {
            player.sendMessage("§7Owner: §fNone");
        }
        player.sendMessage("§7Available: §f" + zone.isAvailable());
        player.sendMessage("§7Location: §f" + zone.getMinX() + ", " + zone.getMinY() + ", " + zone.getMinZ() + " to " + zone.getMaxX() + ", " + zone.getMaxY() + ", " + zone.getMaxZ());

        List<String> allowedPlayers = zone.getAllowedPlayers();
        if (!allowedPlayers.isEmpty()) {
            StringBuilder allowedPlayersString = new StringBuilder();
            allowedPlayersString.append("§7Allowed Players: §f");
            for (int i = 0; i < allowedPlayers.size(); i++) {
                UUID playerUUID = UUID.fromString(allowedPlayers.get(i));
                String playerName = plugin.getServer().getOfflinePlayer(playerUUID).getName();
                allowedPlayersString.append(playerName);
                if (i < allowedPlayers.size() - 1) {
                    allowedPlayersString.append(", ");
                }
            }
            player.sendMessage(allowedPlayersString.toString());
        } else {
            player.sendMessage("§7Allowed Players: §fNone");
        }

        return true;
    }
}
