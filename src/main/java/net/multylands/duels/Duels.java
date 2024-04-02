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
import net.multylands.duels.utils.ConfigUtils;
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

import java.io.*;
import java.util.*;

public class Duels extends JavaPlugin {
    public static HashMap<String, Arena> Arenas = new HashMap<>();
    //storing only sender: requestthatcontainstargetname
    public static HashMap<UUID, Set<DuelRequest>> requestsReceiverToSenders = new HashMap<>();
    public static HashMap<UUID, Set<DuelRequest>> requestsSenderToReceivers = new HashMap<>();
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
    public String ignoresFileName = "ignores.yml";
    public File arenasFile;
    public String arenasFileName = "arenas.yml";
    public File configFile;
    public String configFileName = "config.yml";
    public File languageFile;
    public String languageFileName = "language.yml";
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
            ConfigUtils configUtils = new ConfigUtils(this);
            ignoresFile = new File(getDataFolder(), ignoresFileName);
            arenasFile = new File(getDataFolder(), arenasFileName);
            configFile = new File(getDataFolder(), configFileName);
            languageFile = new File(getDataFolder(), languageFileName);
            //we are checking if files exist to avoid console spamming. try it and see :)
            if (!ignoresFile.exists()) {
                saveResource(ignoresFileName, false);
            }
            if (!languageFile.exists()) {
                saveResource(languageFileName, false);
            }
            if (!configFile.exists()) {
                saveDefaultConfig();
            }
            if (!arenasFile.exists()) {
                saveResource(arenasFileName, false);
            }
            arenasConfig = new YamlConfiguration();
            ignoresConfig = new YamlConfiguration();
            languageConfig = new YamlConfiguration();

            ignoresConfig.load(ignoresFile);
            arenasConfig.load(arenasFile);
            languageConfig.load(languageFile);
            getConfig().load(configFile);
            configUtils.addMissingKeysAndValues(getConfig(), configFileName);
            configUtils.addMissingKeysAndValues(ignoresConfig, ignoresFileName);
            configUtils.addMissingKeysAndValues(arenasConfig, arenasFileName);
            configUtils.addMissingKeysAndValues(languageConfig, languageFileName);
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
            if (!getDescription().getVersion().equals(version)) {
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
            getLogger().info("Server isn't running the PAPER software which means i can't use it's API to deal with shield restrictions. Please switch to paper otherwise Shield restriction will be disabled.");
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
        checkPaper();
        createConfigs();
        implementBStats();
        checkForUpdates();
        manager = new GUIManager(this);
        registerListenersAndCommands();
    }

    @Override
    public void onDisable() {
        for (Set<DuelRequest> requestsSet : requestsReceiverToSenders.values()) {
            for (DuelRequest request : requestsSet) {
                if (!request.getIsInGame()) {
                    continue;
                }
                request.endGame(null, false, true);
            }
        }
        requestsReceiverToSenders.clear();
        requestsSenderToReceivers.clear();
        playerToOpponentInGame.clear();
        Arenas.clear();
        tasksToCancel.clear();
        spectators.clear();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
    public FileConfiguration getConfigFromResource(String resourceName) throws IOException, InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        InputStream stream = getResource(resourceName);
        Reader reader = new InputStreamReader(stream);
        config.load(reader);
        reader.close();
        stream.close();
        return config;
    }
}