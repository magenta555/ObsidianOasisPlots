// src/main/java/com/github/remanso/commands/ZoneInfoCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ZoneInfoCommand implements CommandExecutor, TabCompleter {

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
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        Zone zone;
        if (args.length == 0) {
             zone = plugin.getZones().values().stream()
                    .filter(z -> z.contains(player.getLocation()))
                    .findFirst()
                    .orElse(null);

            if (zone == null) {
                player.sendMessage("You are not standing in a zone.");
                return true;
            }
        } else {
            String zoneName = args[0];
            zone = plugin.getZones().get(zoneName);
            if (zone == null) {
                player.sendMessage("Zone '" + zoneName + "' not found.");
                return true;
            }
        }


        player.sendMessage("§6Zone Info:");
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

     @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            if (player.isOp()) {
                return plugin.getZones().values().stream()
                        .filter(zone -> zone.getName().toLowerCase().startsWith(partialName))
                        .map(Zone::getName)
                        .collect(Collectors.toList());
            } else {
                return plugin.getZones().values().stream()
                        .filter(zone -> (zone.getOwner() != null && zone.getOwner().equals(player.getUniqueId())) || zone.getAllowedPlayers().contains(player.getUniqueId().toString()))
                        .filter(zone -> zone.getName().toLowerCase().startsWith(partialName))
                        .map(Zone::getName)
                        .collect(Collectors.toList());
            }
        }
        return null;
    }
}
