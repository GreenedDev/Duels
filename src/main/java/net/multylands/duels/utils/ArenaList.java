package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;

public class ArenaList {
    public static void store(Arena arena, String arenaID) {
        Duels.Arenas.put(arenaID, arena);
    }
}
