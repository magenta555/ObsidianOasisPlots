// src/main/java/com/github/remanso/commands/ZoneListCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ZoneListCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneListCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zonelist")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        List<Zone> zones;
        if (player.isOp()) {
            zones = plugin.getZones().values().stream().collect(Collectors.toList());
        } else {
            zones = plugin.getZones().values().stream()
                    .filter(zone -> (zone.getOwner() != null && zone.getOwner().equals(player.getUniqueId())) || zone.getAllowedPlayers().contains(player.getUniqueId().toString()))
                    .collect(Collectors.toList());
        }

        if (zones.isEmpty()) {
            player.sendMessage("§cYou are not an owner or allowed player in any zones.");
            return true;
        }

        player.sendMessage("§6Your Zones:");
        zones.forEach(zone -> player.sendMessage("§7- §f" + zone.getName()));
        return true;
    }
}
