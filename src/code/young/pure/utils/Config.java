package code.young.pure.utils;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class Config {

    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private final Plugin plugin;
    private final File file;
    private Configuration config;


    public Config(Plugin plugin) {
        this(plugin, "config.yml");
    }

    public Config(Plugin plugin, String name) {
        this(plugin, new File(plugin.getDataFolder(), name));
    }

    public Config(Plugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        load();
    }

    public void load() {

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, String.format("Could not create config file '%s'.", file.getName()), e);
            }

            try (InputStream in = plugin.getResourceAsStream(file.getName()); OutputStream out = new FileOutputStream(file)) {
                ByteStreams.copy(in, out);
            } catch (FileNotFoundException e) {
                plugin.getLogger().log(Level.SEVERE, String.format("Config file '%s' not found.", file.getName()), e);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, String.format("Could not copy defaults to file '%s'.", file.getName()), e);
            }

        }

        try {
            config = provider.load(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Could not load config '%s'.", file.getName()), e);
        }

    }

    public void save() {
        try {
            provider.save(config, file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Could not save config '%s'.", file.getName()), e);
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }
}