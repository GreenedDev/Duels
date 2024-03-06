package net.multylands.duels;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.multylands.duels.commands.*;
import net.multylands.duels.listeners.GUI;
import net.multylands.duels.listeners.PvP;
import net.multylands.duels.listeners.Spectating;
import net.multylands.duels.listeners.UpdateListener;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.gui.GUIManager;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    //storing only sender: requestthatcontainstargetname
    public static HashMap<UUID, DuelRequest> requests = new HashMap<>();
    //storing sender: player
    //and player: sender
    public static HashMap<UUID, UUID> playerToOpponentInGame = new HashMap<>();
    //storing uuid: taskID
    public static HashMap<UUID, Integer> tasksToCancel = new HashMap<>();
    //storing spectator: toSpectate
    public static HashMap<UUID, UUID> spectators = new HashMap<>();
    public String newVersion = null;
    public int duelInventorySize;
    public File ignoresFile;
    public File arenasFile;
    public File configFile;
    public File languageFile;
    MiniMessage miniMessage;
    public boolean isServerPaper = true;
    public FileConfiguration ignoresConfig;
    public FileConfiguration arenasConfig;
    public FileConfiguration languageConfig;
    public static BukkitScheduler scheduler = Bukkit.getScheduler();
    public GUIManager manager;
    private BukkitAudiences adventure;

    public MiniMessage miniMessage() {
        if (this.miniMessage == null) {
            throw new IllegalStateException("miniMessage is null when getting it from the main class");
        }
        return this.miniMessage;
    }

    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
    public void implementBStats() {
        Metrics metrics = new Metrics(this, 21176);
        metrics.addCustomChart(new SingleLineChart("servers", () -> {
            return 1;
        }));
    }
    private void createConfigs() {
        try {
            ignoresFile = new File(getDataFolder(), "ignores.yml");
            arenasFile = new File(getDataFolder(), "arenas.yml");
            configFile = new File(getDataFolder(), "config.yml");
            languageFile = new File(getDataFolder(), "language.yml");
            //we are checking if files exist to avoid console spamming. try it and see :)
            if (!ignoresFile.exists()) {
                saveResource("ignores.yml", false);
            }
            if (!languageFile.exists()) {
                saveResource("language.yml", false);
            }
            if (!configFile.exists()) {
                saveDefaultConfig();

            }
            if (!arenasFile.exists()) {
                saveResource("arenas.yml", false);
            }
            arenasConfig = new YamlConfiguration();
            ignoresConfig = new YamlConfiguration();
            languageConfig = new YamlConfiguration();

            ignoresConfig.load(ignoresFile);
            arenasConfig.load(arenasFile);
            languageConfig.load(languageFile);
            getConfig().load(configFile);
            for (String arenaID : arenasConfig.getKeys(false)) {
                Location loc1 = arenasConfig.getLocation(arenaID + ".pos1");
                Location loc2 = arenasConfig.getLocation(arenaID + ".pos2");
                Arena arena = new Arena(loc1, loc2, null, null, arenaID);
                Arenas.put(arenaID, arena);
            }
            duelInventorySize = languageConfig.getInt("duel-GUI.size");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveIgnoresConfig() {
        try {
            ignoresConfig.save(ignoresFile);
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
    public void checkForUpdates() {
        new UpdateChecker(this, 114685).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                newVersion = version;
                Chat.sendMessageSender(this, Bukkit.getConsoleSender(), languageConfig.getString("update-available").replace("%newversion%", version));
            }
        });
    }
    public void reloadArenaConfig() {
        arenasFile = new File(getDataFolder(), "arenas.yml");
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        Arenas.clear();
        for (String arenaID : arenasConfig.getKeys(false)) {
            Location loc1 = arenasConfig.getLocation(arenaID + ".pos1");
            Location loc2 = arenasConfig.getLocation(arenaID + ".pos2");
            Arena arena = new Arena(loc1, loc2, null, null, arenaID);
            Arenas.put(arenaID, arena);
        }
    }
    public void checkPaper() {
        boolean isPaper = false;
        try {
            // Any other works, just the shortest I could find.
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {}
        if (!isPaper) {
            getLogger().info("Server isn't running the PAPER software that means i cant use it's API to deal with shield restrictions. Please switch to paper otherwise Shield restriction will be disabled.");
        }
        isServerPaper = isPaper;
    }

    public void reloadLanguageConfig() {
        languageFile = new File(getDataFolder(), "language.yml");
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }
    public void registerListenersAndCommands() {
        getServer().getPluginManager().registerEvents(new GUI(this), this);
        getServer().getPluginManager().registerEvents(new PvP(this), this);
        getServer().getPluginManager().registerEvents(new Spectating(this), this);
        getServer().getPluginManager().registerEvents(new UpdateListener(this), this);
        checkPaper();
        getCommand("duel").setExecutor(new DuelCommand(manager, this));
        getCommand("acceptduel").setExecutor(new AcceptCommand(this));
        getCommand("cancelduel").setExecutor(new CancelCommand(this));
        getCommand("denyduel").setExecutor(new DenyCommand(this));
        getCommand("ignoreduel").setExecutor(new IgnoreCommand(this));
        getCommand("reloadduel").setExecutor(new ReloadCommand(this));
        getCommand("setarenapos").setExecutor(new SetPosCommand(this));
        getCommand("createduelarena").setExecutor(new CreateArenaCommand(this));
        getCommand("setduelspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("stopspectate").setExecutor(new StopSpectateCommand(this));
    }

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        miniMessage = MiniMessage.miniMessage();
        createConfigs();
        implementBStats();
        checkForUpdates();
        manager = new GUIManager(this);
        registerListenersAndCommands();
    }

    @Override
    public void onDisable() {
        for (DuelRequest request : requests.values()) {
            request.endGame(null, false, true);
        }
        requests.clear();
        playerToOpponentInGame.clear();
        Arenas.clear();
        tasksToCancel.clear();
        spectators.clear();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}