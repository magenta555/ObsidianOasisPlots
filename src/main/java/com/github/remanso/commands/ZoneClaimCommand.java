// src/main/java/com/github/remanso/commands/ZoneClaimCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZoneClaimCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneClaimCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zoneclaim")) {
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

        if (!zone.isAvailable()) {
            player.sendMessage("This zone is not available for claiming.");
            return true;
        }

        if (zone.getOwner() != null) {
            player.sendMessage("This zone is already claimed.");
            return true;
        }

        zone.setOwner(player.getUniqueId());
        zone.setAvailable(false);
        plugin.saveZones();

        player.sendMessage("§aYou have claimed this zone.");
        return true;
    }
}
