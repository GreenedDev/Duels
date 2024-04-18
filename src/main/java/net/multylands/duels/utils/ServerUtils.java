package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.commands.DuelAdminCommand;
import net.multylands.duels.commands.DuelCommand;
import net.multylands.duels.commands.admin.ReloadCommand;
import net.multylands.duels.commands.admin.SetSpawnCommand;
import net.multylands.duels.commands.admin.arena.ArenaListCommand;
import net.multylands.duels.commands.admin.arena.CreateArenaCommand;
import net.multylands.duels.commands.admin.arena.DeleteArenaCommand;
import net.multylands.duels.commands.admin.arena.SetPosCommand;
import net.multylands.duels.commands.player.*;
import net.multylands.duels.listeners.GUI;
import net.multylands.duels.listeners.Game;
import net.multylands.duels.listeners.Spectating;
import net.multylands.duels.listeners.UpdateListener;
import net.multylands.duels.placeholders.PlaceholderAPI;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class ServerUtils {
    public static void registerListeners(Duels plugin) {
        plugin.getServer().getPluginManager().registerEvents(new GUI(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Game(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Spectating(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new UpdateListener(plugin), plugin);
    }

    public static void registerCommands(Duels plugin) {
        plugin.getCommand("duel").setExecutor(new DuelCommand(plugin.manager, plugin));
        plugin.getCommand("acceptduel").setExecutor(new AcceptCommand(plugin));
        plugin.getCommand("cancelduel").setExecutor(new CancelCommand(plugin));
        plugin.getCommand("denyduel").setExecutor(new DenyCommand(plugin));
        plugin.getCommand("ignoreduel").setExecutor(new IgnoreCommand(plugin));
        plugin.getCommand("spectate").setExecutor(new SpectateCommand(plugin));
        plugin.getCommand("stopspectate").setExecutor(new StopSpectateCommand(plugin));
        //admin commands
        plugin.getCommand("dueladmin").setExecutor(new DuelAdminCommand(plugin));
        Duels.commandExecutors.put("reload", new ReloadCommand(plugin));
        Duels.commandExecutors.put("setarenapos", new SetPosCommand(plugin));
        Duels.commandExecutors.put("setspawn", new SetSpawnCommand(plugin));
        Duels.commandExecutors.put("createarena", new CreateArenaCommand(plugin));
        Duels.commandExecutors.put("deletearena", new DeleteArenaCommand(plugin));
        Duels.commandExecutors.put("arenalist", new ArenaListCommand(plugin));
    }

    public static boolean isPaper(Duels plugin) {
        boolean isPaper = false;
        try {
            // Any other works, just the shortest I could find.
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {

        }
        return isPaper;
    }

    public static void implementBStats(Duels plugin) {
        Metrics metrics = new Metrics(plugin, 21176);
        metrics.addCustomChart(new SingleLineChart("servers", () -> {
            return 1;
        }));
    }

    public static void implementPlaceholderAPI(Duels plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PlaceholderAPI(plugin).register(); //
        } else {
            plugin.getLogger().log(Level.WARNING, "Could not find PlaceholderAPI! You wouldn't be able to use plugin's placeholders.");
        }
    }
}
