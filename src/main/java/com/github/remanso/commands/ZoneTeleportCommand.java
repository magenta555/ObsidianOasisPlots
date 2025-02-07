// src/main/java/com/github/remanso/commands/ZoneTeleportCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class ZoneTeleportCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneTeleportCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zoneteleport")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /zoneteleport <name>");
            return true;
        }

        String zoneName = args[0];
        Zone zone = plugin.getZones().get(zoneName);

        if (zone == null) {
            player.sendMessage("Zone '" + zoneName + "' not found.");
            return true;
        }

        if (!zone.getOwner().equals(player.getUniqueId()) && !zone.getAllowedPlayers().contains(player.getUniqueId().toString())) {
            player.sendMessage("You are not allowed to teleport to this zone.");
            return true;
        }

        Location teleportLocation = zone.getTeleportLocation();
        if (teleportLocation == null) {
            player.sendMessage("Teleport location for zone '" + zoneName + "' not set.");
            return true;
        }

        player.teleport(teleportLocation);
        player.sendMessage("§aTeleported to zone '" + zoneName + "'.");
        return true;
    }
}
