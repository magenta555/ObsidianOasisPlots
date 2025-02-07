// src/main/java/com/github/remanso/commands/ZoneCreateCommand.java
package com.github.remanso.commands;

import com.github.remanso.Remanso;
import com.github.remanso.model.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class ZoneCreateCommand implements CommandExecutor {

    private final Remanso plugin;

    public ZoneCreateCommand(Remanso plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zonecreate")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /zonecreate <name>");
            return true;
        }

        String zoneName = args[0];
        if (plugin.getZones().containsKey(zoneName)) {
            player.sendMessage("A zone with that name already exists.");
            return true;
        }

        Location pos1 = player.getPersistentDataContainer().get(ZoneToolCommand.POS1_KEY, LocationTagType.TAG);
        Location pos2 = player.getPersistentDataContainer().get(ZoneToolCommand.POS2_KEY, LocationTagType.TAG);

        if (pos1 == null || pos2 == null) {
            player.sendMessage("You must select two corners with the Zoning Tool first.");
            return true;
        }

        if (!pos1.getWorld().equals(pos2.getWorld())) {
            player.sendMessage("The two positions must be in the same world.");
            return true;
        }

        Zone zone = new Zone();
        zone.setName(zoneName);
        zone.setWorldName(pos1.getWorld().getName());
        zone.setMinX(Math.min(pos1.getBlockX(), pos2.getBlockX()));
        zone.setMinY(Math.min(pos1.getBlockY(), pos2.getBlockY()));
        zone.setMinZ(Math.min(pos1.getBlockZ(), pos2.getBlockZ()));
        zone.setMaxX(Math.max(pos1.getBlockX(), pos2.getBlockX()));
        zone.setMaxY(Math.max(pos1.getBlockY(), pos2.getBlockY()));
        zone.setMaxZ(Math.max(pos1.getBlockZ(), pos2.getBlockZ()));
        zone.setOwner(player.getUniqueId());
        zone.setAvailable(false);

        plugin.getZones().put(zoneName, zone);
        plugin.saveZones();

        player.sendMessage("§aZone '" + zoneName + "' created successfully.");
        return true;
    }
}
