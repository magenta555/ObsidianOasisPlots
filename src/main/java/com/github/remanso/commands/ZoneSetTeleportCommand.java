// src/main/java/com/github/remanso/commands/ZoneSetTeleportCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class ZoneSetTeleportCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneSetTeleportCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zonesetteleport")) {
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

        Location playerLocation = player.getLocation();
        zone.setTeleportLocation(playerLocation);
        plugin.saveZones();

        player.sendMessage("§aTeleport location for this zone has been set.");
        return true;
    }
}
