package code.young.pure.management;

import code.young.pure.Pure;
import code.young.pure.database.DatabaseManager;
import code.young.pure.objects.Profile;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class ProfileManager {

    private HashSet<Profile> loadedProfiles = new HashSet<>();
    private Pure instance = Pure.getInstance();
    private DatabaseManager dm = Pure.getInstance().getDatabaseManager();
    private DBCollection pCollection = dm.getProfileCollection();


    public void loadProfiles() {
        DBCursor dbc = pCollection.find();

        while (dbc.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc.next();

            UUID id = UUID.fromString(dbo.getString("uuid"));
            String currentName = dbo.getString("currentName");
            String currentIP = dbo.getString("currentIP");

            BasicDBList dbol3 = (BasicDBList) dbo.get("alts");
            ArrayList<UUID> alts = new ArrayList<>();
            if (dbol3 != null) {
                for (Object obj : dbol3) {
                    alts.add(UUID.fromString((String) obj));
                }
            }

            BasicDBList dbol2 = (BasicDBList) dbo.get("ips");
            ArrayList<String> ips = new ArrayList<>();
            if (dbol2 != null) {
                for (Object obj : dbol2) {
                    ips.add((String) obj);
                }
            }

            BasicDBList dbol1 = (BasicDBList) dbo.get("recentNames");
            ArrayList<String> recentNames = new ArrayList<>();
            if (dbol1 != null) {
                for (Object obj : dbol1) {
                    recentNames.add((String) obj);
                }
            }

            String currentServer = dbo.getString("currentServer");
            String recentServer = dbo.getString("recentServer");
            String createDate = dbo.getString("createDate");
            String password = dbo.getString("password");
            Boolean isOnline = dbo.getBoolean("isOnline");
            Boolean isRegistered = dbo.getBoolean("isRegistered");
            Boolean isTempBanned = dbo.getBoolean("isTempBanned");
            Boolean isPermBanned = dbo.getBoolean("isPermBanned");
            int logins = dbo.getInt("logins");
            int bans = dbo.getInt("bans");
            long lastLogin = dbo.getLong("lastLogin");
            long lastLogout = dbo.getLong("lastLogout");
            long totalTimeOnline = dbo.getLong("totalTimeOnline");

            Profile prof = new Profile(id, currentName, currentIP, alts, ips, recentNames, currentServer, recentServer, password, createDate, isOnline, isRegistered, isTempBanned, isPermBanned, logins, bans, lastLogin, lastLogout, totalTimeOnline);

            getProfiles().add(prof);
        }

        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eSuccessfully loaded &7(&b" + dbc.count() + "&7) &eprofiles."));
        instance.getBanManager().loadBans();
    }

    public void saveProfiles() {
        for (Profile p : getProfiles()) {
            DBCursor dbc = pCollection.find(new BasicDBObject("uuid", p.getUniqueId().toString()));
            BasicDBObject dbo = new BasicDBObject("uuid", p.getUniqueId().toString());
            dbo.put("currentName", p.getCurrentName());
            dbo.put("currentIP", p.getCurrentIP());

            BasicDBList dbl1 = new BasicDBList();
            for (UUID names : p.getAlts()) {
                dbl1.add(names.toString());
            }
            dbo.put("alts", dbl1);

            BasicDBList dbl2 = new BasicDBList();
            for (String names : p.getIps()) {
                dbl2.add(names);
            }
            dbo.put("ips", dbl2);

            BasicDBList dbl3 = new BasicDBList();
            for (String names : p.getRecentNames()) {
                dbl3.add(names);
            }
            dbo.put("recentNames", dbl3);

            dbo.put("currentServer", p.getCurrentServer());
            dbo.put("recentServer", p.getRecentServer());
            dbo.put("createDate", p.getCreatedDate());
            dbo.put("password", p.getPassword());
            dbo.put("isOnline", p.isOnline());
            dbo.put("isRegistered", p.isRegistered());
            dbo.put("isTempBanned", p.isTempBanned());
            dbo.put("isPermBanned", p.isPermBanned());
            dbo.put("logins", p.getLogins());
            dbo.put("bans", p.getBans());
            dbo.put("lastLogin", p.getLastLogin());
            dbo.put("lastLogout", p.getLastLogout());
            dbo.put("totalTimeOnline", p.getTotalTimeOnline());

            if (dbc.hasNext()) {
                pCollection.update(dbc.getQuery(), dbo);
            } else {
                pCollection.insert(dbo);
            }
        }
    }

    public void createProfile(ProxiedPlayer p) {
        String address = p.getAddress().getAddress().getHostAddress().replace("/", "");
        List<UUID> alts = new ArrayList<>();
        List<String> ips = new ArrayList<>();
        ips.add(address);
        List<String> names = new ArrayList<>();
        UUID id = p.getUniqueId();
        String name = p.getName();
        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date dateobj = new Date();

        for (Profile profs : getProfiles()) {
            if (profs.getIps().contains(address)) {
                if (!profs.getAlts().contains(p.getUniqueId())) {
                    profs.getAlts().add(p.getUniqueId());
                }

                alts.add(profs.getUniqueId());
            }
        }

        Profile prof = new Profile(id, name, address, alts, ips, names, "none", "none", "", df.format(dateobj), true, false, false, false, 1, 0, (long) 0, (long) 0, (long) 0);


        BasicDBObject dbo = new BasicDBObject("uuid", prof.getUniqueId().toString());
        dbo.put("currentName", prof.getCurrentName());
        dbo.put("currentIP", prof.getCurrentIP());

        BasicDBList dbl1 = new BasicDBList();
        for (UUID names1 : prof.getAlts()) {
            dbl1.add(names1.toString());
        }
        dbo.put("alts", dbl1);

        BasicDBList dbl2 = new BasicDBList();
        for (String names1 : prof.getIps()) {
            dbl2.add(names1);
        }
        dbo.put("ips", dbl2);

        BasicDBList dbl3 = new BasicDBList();
        for (String names1 : prof.getRecentNames()) {
            dbl3.add(names1);
        }
        dbo.put("recentNames", dbl3);

        dbo.put("currentServer", prof.getCurrentServer());
        dbo.put("recentServer", prof.getRecentServer());
        dbo.put("createDate", prof.getCreatedDate());
        dbo.put("password", prof.getPassword());
        dbo.put("isOnline", prof.isOnline());
        dbo.put("isRegistered", prof.isRegistered());
        dbo.put("isTempBanned", prof.isTempBanned());
        dbo.put("isPermBanned", prof.isPermBanned());
        dbo.put("logins", prof.getLogins());
        dbo.put("bans", prof.getBans());
        dbo.put("lastLogin", prof.getLastLogin());
        dbo.put("lastLogout", prof.getLastLogout());
        dbo.put("totalTimeOnline", prof.getTotalTimeOnline());

        pCollection.insert(dbo);

        prof.setLastLogin(System.currentTimeMillis());

        getProfiles().add(prof);
    }

    public void reloadProfile(Profile prof) {
        saveProfile(prof);
        DBCursor dbc = dm.getProfileCollection().find(new BasicDBObject("uuid", prof.getUniqueId().toString()));
        Profile newProf = null;

        if (dbc.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc.next();

            UUID id = UUID.fromString(dbo.getString("uuid"));
            String currentName = dbo.getString("currentName");
            String currentIP = dbo.getString("currentIP");

            BasicDBList dbol3 = (BasicDBList) dbo.get("alts");
            ArrayList<UUID> alts = new ArrayList<>();
            for (Object obj : dbol3) {
                alts.add(UUID.fromString((String) obj));
            }

            BasicDBList dbol2 = (BasicDBList) dbo.get("ips");
            ArrayList<String> ips = new ArrayList<>();
            if (dbol2 != null) {
                for (Object obj : dbol2) {
                    ips.add((String) obj);
                }
            }

            BasicDBList dbol1 = (BasicDBList) dbo.get("recentNames");
            ArrayList<String> recentNames = new ArrayList<>();
            if (dbol1 != null) {
                for (Object obj : dbol1) {
                    recentNames.add((String) obj);
                }
            }

            String currentServer = dbo.getString("currentServer");
            String recentServer = dbo.getString("recentServer");
            String createDate = dbo.getString("createDate");
            String password = dbo.getString("password");
            Boolean isOnline = dbo.getBoolean("isOnline");
            Boolean isRegistered = dbo.getBoolean("isRegistered");
            Boolean isTempBanned = dbo.getBoolean("isTempBanned");
            Boolean isPermBanned = dbo.getBoolean("isPermBanned");
            int logins = dbo.getInt("logins");
            int bans = dbo.getInt("bans");
            long lastLogin = dbo.getLong("lastLogin");
            long lastLogout = dbo.getLong("lastLogout");
            long totalTimeOnline = dbo.getLong("totalTimeOnline");

            newProf = new Profile(id, currentName, currentIP, alts, ips, recentNames, currentServer, recentServer, password, createDate, isOnline, isRegistered, isTempBanned, isPermBanned, logins, bans, lastLogin, lastLogout, totalTimeOnline);
        }

        if (newProf != null) {
            getProfiles().add(newProf);
            BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eReloaded " + newProf.getCurrentName() + "'s profile."));

            getProfiles().remove(prof);
        } else {
            BungeeCord.getInstance().getLogger().log(Level.SEVERE, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: 5cError: Profile returned was null."));
        }
    }

    public void saveProfile(Profile prof) {
        DBCursor dbc = pCollection.find(new BasicDBObject("uuid", prof.getUniqueId().toString()));
        BasicDBObject dbo = new BasicDBObject("uuid", prof.getUniqueId().toString());
        dbo.put("currentName", prof.getCurrentName());
        dbo.put("currentIP", prof.getCurrentIP());

        BasicDBList dbl1 = new BasicDBList();
        for (UUID names : prof.getAlts()) {
            dbl1.add(names.toString());
        }
        dbo.put("alts", dbl1);

        BasicDBList dbl2 = new BasicDBList();
        for (String names : prof.getIps()) {
            dbl2.add(names);
        }
        dbo.put("ips", dbl2);

        BasicDBList dbl3 = new BasicDBList();
        for (String names : prof.getRecentNames()) {
            dbl3.add(names);
        }
        dbo.put("recentNames", dbl3);

        dbo.put("currentServer", prof.getCurrentServer());
        dbo.put("recentServer", prof.getRecentServer());
        dbo.put("createDate", prof.getCreatedDate());
        dbo.put("password", prof.getPassword());
        dbo.put("isOnline", prof.isOnline());
        dbo.put("isRegistered", prof.isRegistered());
        dbo.put("isTempBanned", prof.isTempBanned());
        dbo.put("isPermBanned", prof.isPermBanned());
        dbo.put("logins", prof.getLogins());
        dbo.put("bans", prof.getBans());
        dbo.put("lastLogin", prof.getLastLogin());
        dbo.put("lastLogout", prof.getLastLogout());
        dbo.put("totalTimeOnline", prof.getTotalTimeOnline());

        if (dbc.hasNext()) {
            pCollection.update(dbc.getQuery(), dbo);
        } else {
            pCollection.insert(dbo);
        }
    }

    public boolean hasProfile(UUID id) {
        for (Profile prof : getProfiles()) {
            if (prof.getUniqueId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public Profile getProfile(UUID id) {
        for (Profile prof : getProfiles()) {
            if (prof.getUniqueId().equals(id)) {
                return prof;
            }
        }
        return null;
    }

    public Profile getProfile(String name) {
        for (Profile prof : getProfiles()) {
            if (prof.getCurrentName().toLowerCase().equals(name.toLowerCase())) {
                return prof;
            }
        }
        return null;
    }

    public HashSet<Profile> getProfiles() {
        return loadedProfiles;
    }
}