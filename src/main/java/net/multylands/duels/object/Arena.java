package net.multylands.duels.object;

import net.multylands.duels.utils.ArenaList;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arena {
    public boolean isAvailable = true;
    public UUID sender;
    public UUID target;
    public String ID;
    public Location loc1;
    public Location loc2;
    public Arena(Location loc1, Location loc2, UUID sender, UUID target, String ID) {
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.sender = sender;
        this.target = target;
        this.ID = ID;
    }
    public Boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean value) {
        this.isAvailable = value;
        ArenaList.store(this, getID());
    }
    public Location getFirstLocation() {
        return loc1;
    }
    public Location getSecondLocation() {
        return loc2;
    }
    public String getID() {
        return ID;
    }
    public UUID getSenderUUID() {
        return sender;
    }
    public UUID getTargetUUID() {
        return target;
    }
}
