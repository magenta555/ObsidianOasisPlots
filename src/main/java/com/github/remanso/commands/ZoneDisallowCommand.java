// src/main/java/com/github/remanso/commands/ZoneDisallowCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

public class ZoneDisallowCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneDisallowCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zonedisallow")) {
            player.sendMessage("§dYou do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /zonedisallow <player>");
            return true;
        }

        String targetPlayerName = args[0];
        OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.hasPlayedBefore()) {
            player.sendMessage("Player '" + targetPlayerName + "' not found.");
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

        if (!zone.getOwner().equals(player.getUniqueId())) {
            player.sendMessage("§dYou do not own this zone.");
            return true;
        }

        String targetPlayerUUID = targetPlayer.getUniqueId().toString();
        if (!zone.getAllowedPlayers().contains(targetPlayerUUID)) {
            player.sendMessage("Player '" + targetPlayerName + "' is not allowed in this zone.");
            return true;
        }

        zone.getAllowedPlayers().remove(targetPlayerUUID);
        plugin.saveZones();
        player.sendMessage("§aPlayer '" + targetPlayerName + "' is no longer allowed in this zone.");
        return true;
    }
}
