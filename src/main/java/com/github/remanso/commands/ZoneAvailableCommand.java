// src/main/java/com/github/remanso/commands/ZoneAvailableCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZoneAvailableCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneAvailableCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zoneavailable")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        Zone zone = plugin.getZones().values().stream()
                .filter(z -> z.contains(player.getLocation()))
                .findFirst()
                .orElse(null);

        if (zone == null) {
            player.sendMessage("You are not standing in a zone.");
            return true;
        }

        if (!zone.getOwner().equals(player.getUniqueId())) {
            player.sendMessage("You do not own this zone.");
            return true;
        }

        zone.setOwner(null); // Clear the current owner
        zone.getAllowedPlayers().clear(); // Clear the current allowed players
        zone.setAvailable(true);
        plugin.saveZones();
        player.sendMessage("§aThis zone is now available for claiming.");
        return true;
    }
}
