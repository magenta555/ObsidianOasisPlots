// src/main/java/com/github/remanso/commands/LocationTagType.java
package com.github.remanso.commands;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class LocationTagType implements PersistentDataType<String, Location> {
    public static final LocationTagType TAG = new LocationTagType();
    public static final NamespacedKey POS1_KEY = new NamespacedKey("remanso", "pos1");
    public static final NamespacedKey POS2_KEY = new NamespacedKey("remanso", "pos2");

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Location> getComplexType() {
        return Location.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull Location location, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    @NotNull
    @Override
    public Location fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        String[] split = s.split(";");
        return new Location(org.bukkit.Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }
}
