package com.github.remanso;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Material;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.bukkit.entity.HumanEntity;

public class MinecraftGlass extends JavaPlugin {
  public void onEnable() {
    saveResource("config.yml", false);
    Bukkit.getPluginManager().registerEvents(new AirReplaceGlass(this), this);
    this.getCommand("remanso").setExecutor(this);
    this.getCommand("r").setExecutor(this);
  }

  public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
    if (a.length == 0) {
      s.sendMessage("Remanso, version 0, by magenta555");
      return true;
    }
    if (a[0].equals("reload")) {
        reloadConfig();
        s.sendMessage("Reloaded Remanso config.yml");
        return true;
    }
    if (a[0].equals("tool")) {
        Player player = Bukkit.getServer().getPlayer(s.getName());
        ItemStack itemStack = new ItemStack(Material.matchMaterial(getConfig().getString("remanso.tool.material")));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getConfig().getString("remanso.tool.name"));
        List<String> loreList = new ArrayList<String>();
        loreList.add(getConfig().getString("remanso.tool.lore"));
        itemMeta.setLore(loreList);
        if (getConfig().getString("remanso.tool.glint").equals("true")) {
            itemMeta.setEnchantmentGlintOverride(true);
        }
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
        s.sendMessage("Added the item to the player's inventory!");
    }
        return true;
    }
  }

  public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
    return true;
  }
}