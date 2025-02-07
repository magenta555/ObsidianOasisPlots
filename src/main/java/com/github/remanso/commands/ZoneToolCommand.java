// src/main/java/com/github/remanso/commands/ZoneToolCommand.java
package com.github.remanso.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ZoneToolCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("remanso.zonetool")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        ItemStack zoneTool = new ItemStack(Material.STICK);
        ItemMeta meta = zoneTool.getItemMeta();
        meta.setDisplayName("§bZoning Tool");
        meta.setLore(Collections.singletonList("§7Used to select corners for zones."));
        zoneTool.setItemMeta(meta);

        player.getInventory().addItem(zoneTool);
        player.sendMessage("§aYou have been given a Zoning Tool.");
        return true;
    }
}
