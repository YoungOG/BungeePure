package code.young.pure;

import code.young.pure.commands.*;
import code.young.pure.database.DatabaseManager;
import code.young.pure.listeners.AntiSpamBot;
import code.young.pure.listeners.LoginListener;
import code.young.pure.management.BanManager;
import code.young.pure.management.ProfileManager;
import code.young.pure.objects.Profile;
import code.young.pure.utils.Config;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Pure extends Plugin {

    private static Pure instance;
    private Config config;
    private DatabaseManager dm;
    private ProfileManager pm;
    private BanManager bm;
    private boolean locked;

    public static Pure getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        locked = true;
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &cThe Network lock has been enabled."));

        config = new Config(this);
        config.load();

        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eLoading configuration settings:"));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eDatabase Host&7: &b" + config.getConfig().getString("database.host")));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &ePrefix&7: &b" + config.getConfig().getString("settings.prefix")));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eNetwork Name&7: &b" + config.getConfig().getString("settings.network-name")));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eWebstore&7: &b" + config.getConfig().getString("settings.webstore")));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eRegister Link&7: &b" + config.getConfig().getString("register.register-link")));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eAnti-Spambot Enabled&7: &b" + config.getConfig().getBoolean("anti-spambot.enabled")));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eAnti-Spambot Limit&7: &b" + config.getConfig().getInt("anti-spambot.account-limit")));

        dm = new DatabaseManager();
        dm.connect();

        pm = new ProfileManager();
        bm = new BanManager();
        pm.loadProfiles();

        getProxy().getPluginManager().registerCommand(this, new ProfileCommand());
        getProxy().getPluginManager().registerCommand(this, new BanCommand());
        getProxy().getPluginManager().registerCommand(this, new TempBanCommand());
        getProxy().getPluginManager().registerCommand(this, new BanIPCommand());
        getProxy().getPluginManager().registerCommand(this, new TempBanIPCommand());
        getProxy().getPluginManager().registerCommand(this, new UnbanCommand());
        getProxy().getPluginManager().registerCommand(this, new UnbanIPCommand());
        getProxy().getPluginManager().registerCommand(this, new SeenCommand());
        getProxy().getPluginManager().registerCommand(this, new BanInfoCommand());
        getProxy().getPluginManager().registerCommand(this, new HubCommand());
        getProxy().getPluginManager().registerCommand(this, new NetChatCommand());
        getProxy().getPluginManager().registerCommand(this, new RegisterCommand());

        if (config.getConfig().getBoolean("anti-spambot.enabled")) {
            getProxy().getPluginManager().registerListener(this, new AntiSpamBot());
        }

        getProxy().getPluginManager().registerListener(this, new LoginListener());

        getProxy().getScheduler().schedule(this, new Runnable() {
            public void run() {
                for (Profile prof : pm.getProfiles()) {
                    if (prof.isOnline()) {
                        prof.setTotalTimeOnline(prof.getTotalTimeOnline() + 1);
                    }
                }
            }
        }, 0L, 1, TimeUnit.MILLISECONDS);
    }

    public void onDisable() {
        pm.saveProfiles();

        dm.getDatabase().getMongo().close();
    }

    public Config getConfiguration() {
        return config;
    }

    public ProfileManager getProfileManager() {
        return pm;
    }

    public DatabaseManager getDatabaseManager() {
        return dm;
    }

    public BanManager getBanManager() {
        return bm;
    }

    public Boolean isLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}