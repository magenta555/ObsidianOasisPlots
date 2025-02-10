// src/main/java/com/github/remanso/commands/ZoneDeleteCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZoneDeleteCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneDeleteCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zonedelete")) {
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

        // Check for OP player bypass
        if (player.isOp()) {
            // OP players can delete any zone, even without an owner
            plugin.getZones().remove(zone.getName());
            plugin.saveZones();
            player.sendMessage("§dZone '" + zone.getName() + "' deleted successfully.");
            return true;
        }

        //Normal flow
        if (zone.getOwner() == null) {
            player.sendMessage("§dThis zone has no owner and cannot be deleted by players. OP players can delete it.");
            return true;
        }


        if (!zone.getOwner().equals(player.getUniqueId())) {
            player.sendMessage("§dYou do not own this zone.");
            return true;
        }

        plugin.getZones().remove(zone.getName());
        plugin.saveZones();
        player.sendMessage("§aZone '" + zone.getName() + "' deleted successfully.");
        return true;
    }
}
