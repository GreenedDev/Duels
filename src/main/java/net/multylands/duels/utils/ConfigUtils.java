package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {
    Duels plugin;

    public ConfigUtils(Duels plugin) {
        this.plugin = plugin;
    }

    public void addMissingKeysAndValues(FileConfiguration config, String fileName) {
        List<String> KeysAndValues = getKeysAndValues(fileName);
        if (KeysAndValues.isEmpty()) {
        }
        for (String key : config.getKeys(true)) {
            KeysAndValues.remove(key);
        }
        if (KeysAndValues.isEmpty()) {
            return;
        }
        FileConfiguration defaultConfig;
        try {
            defaultConfig = plugin.getConfigFromResource(fileName);
            for (String actuallyMissingKey : KeysAndValues) {
                config.set(actuallyMissingKey, defaultConfig.get(actuallyMissingKey));
                File configFile = new File(plugin.getDataFolder(), fileName);
                config.save(configFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getKeysAndValues(String resourceName) {
        List<String> keys = new ArrayList<>();
        FileConfiguration config = new YamlConfiguration();
        try {
            config = plugin.getConfigFromResource(resourceName);
        } catch (InvalidConfigurationException | IOException e) {
            System.out.println(e);
        }
        for (String key : config.getKeys(true)) {
            keys.add(key);
        }
        return keys;
    }

}
