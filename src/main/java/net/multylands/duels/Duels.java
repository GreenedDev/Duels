package net.multylands.duels;

import net.multylands.duels.commands.*;
import net.multylands.duels.gui.DuelInventoryHolder;
import net.multylands.duels.listeners.GUI;
import net.multylands.duels.listeners.PvP;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.gui.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Duels extends JavaPlugin {
    public static HashMap<String, Arena> Arenas = new HashMap<>();

    public static HashMap<UUID, DuelRequest> requests = new HashMap<>();
    public static HashMap<Integer, Integer> tasksToCancel = new HashMap<>();
    public static HashMap<UUID, UUID> SenderToTarget = new HashMap<>();
    public int duelInventorySize = 27;
    public File ignoresConfigFile;
    public File arenasFile;
    public File configFile;
    public FileConfiguration ignoresConfig;
    public FileConfiguration arenasConfig;
    public static BukkitScheduler scheduler = Bukkit.getScheduler();
    public GUIManager manager;

    private void createConfigs() {
        try {
            ignoresConfigFile = new File(getDataFolder(), "ignores.yml");
            arenasFile = new File(getDataFolder(), "arenas.yml");
            configFile = new File(getDataFolder(), "config.yml");
            if (!ignoresConfigFile.exists()) {
                ignoresConfigFile.getParentFile().mkdirs();
                ignoresConfigFile.createNewFile();
            }
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                Location spawnLoc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
                getConfig().set("spawn_location", spawnLoc);
                getConfig().set("max_duel_time_minutes", 10);
                saveConfig();

            }
            if (!arenasFile.exists()) {
                arenasFile.getParentFile().mkdirs();
                arenasFile.createNewFile();
                World world = Bukkit.getWorld("world");
                Location loc1 = new Location(world, 100,70,100,90,0);
                Location loc2 = new Location(world, 90,70,100,-90,0);
                arenasConfig = new YamlConfiguration();
                arenasConfig.set("1.pos1", loc1);
                arenasConfig.set("1.pos2", loc2);

                saveArenasConfig();
            }
            arenasConfig = new YamlConfiguration();
            ignoresConfig = new YamlConfiguration();

            ignoresConfig.load(ignoresConfigFile);
            arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
            getConfig().load(configFile);
            for (String arenaID : arenasConfig.getKeys(false)) {
                Location loc1 = arenasConfig.getLocation(arenaID+".pos1");
                Location loc2 = arenasConfig.getLocation(arenaID+".pos2");
                Arena arena = new Arena(loc1, loc2, null, null, arenaID);
                Arenas.put(arenaID, arena);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void saveIgnoresConfig() {
        try {
            ignoresConfig.save(ignoresConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void saveArenasConfig() {
        try {
            arenasConfig.save(arenasFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void reloadArenaConfig() {
        arenasFile = new File(getDataFolder(), "arenas.yml");
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        Arenas.clear();
        for (String arenaID : arenasConfig.getKeys(false)) {
            Location loc1 = arenasConfig.getLocation(arenaID+".pos1");
            Location loc2 = arenasConfig.getLocation(arenaID+".pos2");
            Arena arena = new Arena(loc1, loc2, null, null, arenaID);
            Arenas.put(arenaID, arena);
        }
    }
    @Override
    public void onEnable() {
        createConfigs();
        manager = new GUIManager(this);
        getServer().getPluginManager().registerEvents(new GUI(this), this);
        getServer().getPluginManager().registerEvents(new PvP(this), this);
        getCommand("duel").setExecutor(new DuelCommand(manager, this));
        getCommand("acceptduel").setExecutor(new AcceptCommand(this));
        getCommand("cancelduel").setExecutor(new CancelCommand());
        getCommand("denyduel").setExecutor(new DenyCommand());
        getCommand("ignoreduel").setExecutor(new IgnoreDuel(this));
        getCommand("reloadduel").setExecutor(new ReloadCommand(this));
    }

    @Override
    public void onDisable() {
        requests.clear();
        SenderToTarget.clear();
        Arenas.clear();
    }
}