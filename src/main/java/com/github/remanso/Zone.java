package com.github.remanso;

import org.bukkit.Location;
import java.util.UUID;

public class Zone {
    private final UUID ownerUUID;
    private final Location pos1;
    private final Location pos2;
    private final String zoneName; // Zone Name

    public Zone(UUID ownerUUID, Location pos1, Location pos2, String zoneName) {
        this.ownerUUID = ownerUUID;
        this.pos1 = pos1.clone();
        this.pos2 = pos2.clone();
        this.zoneName = zoneName;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public Location getPos1() {
        return pos1.clone();
    }

    public Location getPos2() {
        return pos2.clone();
    }

    public String getZoneName() {
        return zoneName;
    }
}
