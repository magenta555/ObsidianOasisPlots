// Defines the package for the class
package com.github.remanso.model;

// Imports the Location class from Bukkit API
import org.bukkit.Location;

// Imports ArrayList and List classes from Java's util package
import java.util.ArrayList;
import java.util.List;
// Imports the UUID class from Java's util package
import java.util.UUID;

// Defines the Zone class
public class Zone {
    // Declares fields for zone properties
    private String name;  // Name of the zone
    private String worldName;  // Name of the world where the zone is located
    private int minX;  // Minimum X coordinate of the zone
    private int minY;  // Minimum Y coordinate of the zone
    private int minZ;  // Minimum Z coordinate of the zone
    private int maxX;  // Maximum X coordinate of the zone
    private int maxY;  // Maximum Y coordinate of the zone
    private int maxZ;  // Maximum Z coordinate of the zone
    private UUID owner;  // UUID of the owner of the zone
    private boolean available;  // Availability status of the zone
    private List<String> allowedPlayers = new ArrayList<>();  // List of allowed players in the zone
    private Location teleportLocation;  // Teleport location for this zone

    // Getter for name property
    public String getName() {
        return name;
    }

    // Setter for name property
    public void setName(String name) {
        this.name = name;
    }

    // Getter for worldName property
    public String getWorldName() {
        return worldName;
    }

    // Setter for worldName property
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    // Getter for minX property
    public int getMinX() {
        return minX;
    }

    // Setter for minX property
    public void setMinX(int minX) {
        this.minX = minX;
    }

    // Getter for minY property
    public int getMinY() {
        return minY;
    }

    // Setter for minY property
    public void setMinY(int minY) {
        this.minY = minY;
    }

    // Getter for minZ property
    public int getMinZ() {
        return minZ;
    }

    // Setter for minZ property
    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    // Getter for maxX property
    public int getMaxX() {
        return maxX;
    }

    // Setter for maxX property
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    // Getter for maxY property
    public int getMaxY() {
        return maxY;
    }

    // Setter for maxY property
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    // Getter for maxZ property
    public int getMaxZ() {
        return maxZ;
    }

    // Setter for maxZ property
    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    // Getter for owner property
    public UUID getOwner() {
        return owner;
    }

    // Setter for owner property
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    // Getter for availability status 
    public boolean isAvailable() {
        return available;
    }

   // Setter for availability status 
   public void setAvailable(boolean available) {
       this.available = available;
   }

   // Getter for allowed players list 
   public List<String> getAllowedPlayers() {
       return allowedPlayers;
   }

   // Setter for allowed players list 
   public void setAllowedPlayers(List<String> allowedPlayers) {
       this.allowedPlayers = allowedPlayers;
   }

   // Getter for teleport location 
   public Location getTeleportLocation() {
       return teleportLocation;
   }

   // Setter for teleport location 
   public void setTeleportLocation(Location teleportLocation) {
       this.teleportLocation = teleportLocation;
   }

   // Method to check if a location is within this zone 
   public boolean contains(Location location) {
       // Checks if the location's world matches this zone's world 
       if (!location.getWorld().getName().equals(worldName)) {
           return false;  // Return false if worlds do not match 
       }
       int x = location.getBlockX();  // Get block X coordinate 
       int y = location.getBlockY();  // Get block Y coordinate 
       int z = location.getBlockZ();  // Get block Z coordinate 
       
       // Checks if coordinates are within the defined bounds of the zone 
       return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
   }
}
