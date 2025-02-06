package com.github.remanso;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import net.kyori.adventure.text.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Remanso extends JavaPlugin {

    private ZoneManager zoneManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        zoneManager = new ZoneManager(this);

        // Register Event Listeners
        getServer().getPluginManager().registerEvents(new ZoneToolListener(this, zoneManager), this);
        getServer().getPluginManager().registerEvents(new ZoneEnterExitListener(this, zoneManager), this);

        // Set Command Executor and Tab Completer
        getCommand("remanso").setExecutor(this);
        getCommand("remanso").setTabCompleter(this);
        getCommand("r").setExecutor(this);
        getCommand("r").setTabCompleter(this);
    }

    @Override
    public void onDisable() {
        zoneManager.saveZonesToConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("remanso") || command.getName().equalsIgnoreCase("r")) {
            if (args.length == 0) {
                sender.sendMessage("§d[Remanso] Remanso, version 0, created by magenta555");
                sender.sendMessage("§d[Remanso] Land zoning plugin for Minecraft servers!");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "zone":
                    if (args.length >= 3 && args[1].equalsIgnoreCase("create")) {
                        StringBuilder zoneNameBuilder = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            zoneNameBuilder.append(args[i]).append(" ");
                        }
                        String zoneName = zoneNameBuilder.toString().trim();

                        if (zoneName.isEmpty()) {
                            sender.sendMessage("§cUsage: /remanso zone create [name]");
                            return true;
                        }
                        return zoneManager.createZoneCommand(sender, zoneName);
                    } else {
                        sender.sendMessage("§cUsage: /remanso zone create [name]");
                        return true;
                    }
                case "tool":
                    giveZoneTool(sender);
                    return true;
                case "reload":
                    reloadConfig(sender);
                    return true;
                default:
                    sender.sendMessage("§cUnknown command. Use /remanso for help.");
                    return false;
            }
        }
        return false;
    }

    private void giveZoneTool(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by a player.");
            return;
        }

        Player player = (Player) sender;
        Material material = Material.matchMaterial(getConfig().getString("remanso.tool.material"));

        if (material == null) {
            player.sendMessage("§cInvalid material specified in config.");
            return;
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            // Use Adventure API for display name
            String displayName = getConfig().getString("remanso.tool.name");
            itemMeta.displayName(Component.text(displayName == null ? "" : displayName));

            // Use Adventure API for lore
            List<String> loreConfig = getConfig().getStringList("remanso.tool.lore");
            List<Component> lore = loreConfig.stream()
                    .map(s -> Component.text(s == null ? "" : s))
                    .collect(Collectors.toList());
            itemMeta.lore(lore);

            if (getConfig().getBoolean("remanso.tool.glint", false)) {
                itemMeta.setEnchantmentGlintOverride(true);
            }

            itemStack.setItemMeta(itemMeta);
            player.getInventory().addItem(itemStack);
            player.sendMessage("§d[Remanso] You should now have a Zoning Tool!");
        } else {
            player.sendMessage("§cFailed to create item meta.");
        }
    }

    private void reloadConfig(CommandSender sender) {
        reloadConfig();
        sender.sendMessage("§d[Remanso] Reloaded Remanso config.yml");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("remanso") || command.getName().equalsIgnoreCase("r")) {
            if (args.length == 1) {
                completions.add("help");
                completions.add("reload");
                completions.add("teleport");
                completions.add("teleport-location");
                completions.add("tool");
                completions.add("zone");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("zone")) {
                completions.add("create");
            }
        }
        return completions;
    }
}
