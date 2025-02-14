// This line declares the package for the class
package com.github.remanso.commands;

// Imports the Location class from the Bukkit API
import org.bukkit.Location;

// Imports the NamespacedKey class from the Bukkit API
import org.bukkit.NamespacedKey;

// Imports the PersistentDataAdapterContext class from the Bukkit API
import org.bukkit.persistence.PersistentDataAdapterContext;

// Imports the PersistentDataType interface from the Bukkit API
import org.bukkit.persistence.PersistentDataType;

// Imports the NotNull annotation from JetBrains
import org.jetbrains.annotations.NotNull;

// This line defines a public class named LocationTagType that implements PersistentDataType
public class LocationTagType implements PersistentDataType<String, Location> {
    // This line declares a static final instance of LocationTagType
    public static final LocationTagType TAG = new LocationTagType();
    
    // This line declares a static final NamespacedKey for position 1
    public static final NamespacedKey POS1_KEY = new NamespacedKey("remanso", "pos1");
    
    // This line declares a static final NamespacedKey for position 2
    public static final NamespacedKey POS2_KEY = new NamespacedKey("remanso", "pos2");

    // This method returns the primitive type of the data (String)
    @Override
    public @NotNull Class<String> getPrimitiveType() {
        // Returns the String class type
        return String.class;
    }

    // This method returns the complex type of the data (Location)
    @Override
    public @NotNull Class<Location> getComplexType() {
        // Returns the Location class type
        return Location.class;
    }

    // This method converts a Location object to its primitive String representation
    @NotNull
    @Override
    public String toPrimitive(@NotNull Location location, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        // Constructs and returns a String representation of the location's world name and coordinates
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    // This method converts a primitive String back into a Location object
    @NotNull
    @Override
    public Location fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        // Splits the input string into an array using ";" as a delimiter
        String[] split = s.split(";");
        
        // Creates and returns a new Location object based on the parsed data from the string array
        return new Location(org.bukkit.Bukkit.getWorld(split[0]), 
                            Double.parseDouble(split[1]), 
                            Double.parseDouble(split[2]), 
                            Double.parseDouble(split[3]), 
                            Float.parseFloat(split[4]), 
                            Float.parseFloat(split[5]));
    }
}
