package code.young.pure.database;

import code.young.pure.Pure;
import code.young.pure.utils.Config;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

import java.net.UnknownHostException;
import java.util.logging.Level;

public class DatabaseManager {

    private DB db;
    private Config cm = Pure.getInstance().getConfiguration();

    public void connect() {
        try {
            db = MongoClient.connect(new DBAddress(cm.getConfig().getString("database.host"), "pure"));
            ProxyServer.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&bPure&7]: &eSuccessfully connected to MongoDB."));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            ProxyServer.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&bPure&7]: &eFailed to connect to MongoDB."));
        }
    }

    public DB getDatabase() {
        return db;
    }

    public DBCollection getProfileCollection() {
        return db.getCollection("profile");
    }

    public DBCollection getPermBans() {
        return db.getCollection("permbans");
    }

    public DBCollection getTempBans() {
        return db.getCollection("tempbans");
    }

    public DBCollection getPermIPBans() {
        return db.getCollection("ipbans");
    }

    public DBCollection getTempIPBans() {
        return db.getCollection("tempipbans");
    }
}
