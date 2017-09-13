package code.young.pure.objects;

import code.young.pure.Pure;
import code.young.pure.database.DatabaseManager;
import code.young.pure.management.ProfileManager;
import code.young.pure.utils.DateUtil;
import code.young.pure.utils.MessageUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Profile {

    private Pure main = Pure.getInstance();
    private ProfileManager pm = main.getProfileManager();
    private DatabaseManager dm = main.getDatabaseManager();

    UUID id;
    String currentName;
    String currentIP;
    String currentServer;
    String recentServer;
    String createDate;
    String password;
    List<String> recentNames, ips;
    List<UUID> alts;
    boolean isOnline;
    boolean isRegistered;
    boolean isPermBanned;
    boolean isTempBanned;
    int logins;
    int bans;
    long lastLogin;
    long lastLogout;
    long totalTimeOnline;

    public Profile(UUID id,
                   String currentName, String currentIP,
                   List<UUID> alts, List<String> ips, List<String> recentNames,
                   String currentServer, String recentServer, String password, String createDate,
                   Boolean isOnline, Boolean isRegistered, Boolean isTempBanned, Boolean isPermBanned,
                   Integer logins, Integer bans,
                   long lastLogin, long lastLogout, long totalTimeOnline) {
        this.id = id;
        this.currentName = currentName;
        this.currentIP = currentIP;
        this.alts = alts;
        this.ips = ips;
        this.recentNames = recentNames;
        this.currentServer = currentServer;
        this.recentServer = recentServer;
        this.isOnline = isOnline;
        this.password = password;
        this.isRegistered = isRegistered;
        this.isTempBanned = isTempBanned;
        this.isPermBanned = isPermBanned;
        this.logins = logins;
        this.bans = bans;
        this.totalTimeOnline = totalTimeOnline;
        this.createDate = createDate;
        this.lastLogin = lastLogin;
        this.lastLogout = lastLogout;
    }

    public UUID getUniqueId() {
        return id;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public String getCurrentIP() {
        return currentIP;
    }

    public void setAddress(String currentIP) {
        this.currentIP = currentIP;
    }

    public List<UUID> getAlts() {
        return alts;
    }

    public List<String> getIps() {
        return ips;
    }

    public List<String> getRecentNames() {
        return recentNames;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }

    public String getRecentServer() {
        return recentServer;
    }

    public void setRecentServer(String recentServer) {
        this.recentServer = recentServer;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public boolean isTempBanned() {
        return isTempBanned;
    }

    public void setTempBanned(boolean isTempBanned) {
        this.isTempBanned = isTempBanned;
    }

    public boolean isPermBanned() {
        return isPermBanned;
    }

    public void setPermBanned(boolean isPermBanned) {
        this.isPermBanned = isPermBanned;
    }

    public String getCreatedDate() {
        return createDate;
    }

    public int getLogins() {
        return logins;
    }

    public void setLogins(int logins) {
        this.logins = logins;
    }

    public int getBans() {
        return bans;
    }

    public void setBans(int bans) {
        this.bans = bans;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(long lastLogout) {
        this.lastLogout = lastLogout;
    }

    public long getTotalTimeOnline() {
        return totalTimeOnline;
    }

    public void setTotalTimeOnline(long totalTimeOnline) {
        this.totalTimeOnline = totalTimeOnline;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void sendMessage(String message) {
        if (BungeeCord.getInstance().getPlayer(getUniqueId()) != null) {
            BungeeCord.getInstance().getPlayer(getUniqueId()).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public void sendMessage(TextComponent tc) {
        if (BungeeCord.getInstance().getPlayer(getUniqueId()) != null) {
            BungeeCord.getInstance().getPlayer(getUniqueId()).sendMessage(tc);
        }
    }

    public void sendMessage(TextComponent... tc) {
        if (BungeeCord.getInstance().getPlayer(getUniqueId()) != null) {
            BungeeCord.getInstance().getPlayer(getUniqueId()).sendMessage(tc);
        }
    }

    public void information(CommandSender p) {
        MessageUtil.sendMessage(p, "&8&m------&r &d" + getCurrentName() + " &8&m------");
        MessageUtil.sendMessage(p, "&eAddress&7: &b" + getCurrentIP());
        MessageUtil.sendMessage(p, "&eDate Created&7: &b" + getCreatedDate());
        MessageUtil.sendMessage(p, "&eRecent Names&7: " + (getRecentNames().size() > 0 ? "(&b" + getRecentNames().size() + "&7): &b" + getRecentNames().toString().replace("[", "").replace("]", "") : "(&b0&7)"));

        List<String> altnames = new ArrayList<>();
        for (UUID ids : getAlts()) {
            altnames.add(pm.getProfile(ids).getCurrentName());
        }
        MessageUtil.sendMessage(p, "&eKnown Alts&7: " + (altnames.size() > 0 ? "(&b" + altnames.size() + "&7): &b" + altnames.toString().replace("[", "").replace("]", "") : "(&b0&7)"));
        MessageUtil.sendMessage(p, "&eKnown IPs&7: " + (getIps().size() > 0 ? "(&b" + getIps().size() + "&7): &b" + getIps().toString().replace("[", "").replace("]", "") : "(&b0&7)"));
        MessageUtil.sendMessage(p, "&eCurrent Server&7: &b" + getCurrentServer());
        MessageUtil.sendMessage(p, "&eRecent Server&7: &b" + getRecentServer());
        MessageUtil.sendMessage(p, "&eIs Online&7: &b" + isOnline());
        MessageUtil.sendMessage(p, "&eTotal Playtime&7: &b" + DateUtil.formatDateDiff((System.currentTimeMillis() - getLastLogin() + getTotalTimeOnline())));
        MessageUtil.sendMessage(p, "&eIs Registed&7: &b" + isRegistered());
        MessageUtil.sendMessage(p, "&eTotal Logins&7: &b" + getLogins());
        MessageUtil.sendMessage(p, "&eTotal Bans&7: &b" + getBans());
    }

    public void seen(CommandSender p) {
        MessageUtil.sendMessage(p, "&8&m------&r &d" + getCurrentName() + " &8&m------");
        MessageUtil.sendMessage(p, "&eOnline&7: " + (isOnline() ? "&a" + isOnline() : "&c" + isOnline()));
        MessageUtil.sendMessage(p, (isOnline() ? "&eTime Online&7: &b" + DateUtil.formatDateDiff(getLastLogin()) + "." : "&eTime Offline&7: &b" + DateUtil.formatDateDiff(getLastLogout())));
        MessageUtil.sendMessage(p, (isOnline() ? "&eCurrent IP&7: &b" + getCurrentIP() : "&eLast IP&7: &b" + getCurrentIP()));
        MessageUtil.sendMessage(p, "&eTotal Playtime&7: &b" + readableTime(getTotalTimeOnline()));
        if (!isOnline()) {
            if (!isPermBanned() && !isTempBanned()) {
                MessageUtil.sendMessage(p, "&eBanned&7: &bfalse");
                return;
            }

            if (isPermBanned()) {
                MessageUtil.sendMessage(p, "&eBanned&7: &ctrue - Permanent");

                DBCursor dbc = dm.getPermBans().find(new BasicDBObject("uuid", getUniqueId().toString()));

                if (dbc.hasNext()) {
                    BasicDBObject dbo = (BasicDBObject) dbc.next();

                    MessageUtil.sendMessage(p, "&eReason&7: &c" + dbo.getString("reason"));
                    MessageUtil.sendMessage(p, "&eBanned by&7: &c" + dbo.getString("banner"));
                    MessageUtil.sendMessage(p, "&eIssued&7: &c" + dbo.getString("date"));
                }
            }
            if (isTempBanned()) {
                MessageUtil.sendMessage(p, "&eBanned&7: &ctrue - Temporary");

                DBCursor dbc = dm.getTempBans().find(new BasicDBObject("uuid", getUniqueId().toString()));
                if (dbc.hasNext()) {
                    BasicDBObject dbo = (BasicDBObject) dbc.next();

                    MessageUtil.sendMessage(p, "&eReason&7: &c" + dbo.get("reason"));
                    MessageUtil.sendMessage(p, "&eBanned by&7: &c" + dbo.get("banner"));
                    MessageUtil.sendMessage(p, "&eBan Lenght&7: &c" + DateUtil.formatDateDiff(dbo.getLong("banned_until")));
                    MessageUtil.sendMessage(p, "&eIssued&7: &c" + dbo.getString("date"));
                }
            }
        }

        List<String> alts = new ArrayList<>();

        for (Profile profs : pm.getProfiles()) {
            if (profs.getCurrentIP().equals(getCurrentIP())) {
                if (profs != this) {
                    if (profs.isOnline()) {
                        alts.add("&b" + profs.getCurrentName());
                    } else {
                        alts.add("&c" + profs.getCurrentName());
                    }
                }
            }
        }

        if (alts.size() > 0) {
            MessageUtil.sendMessage(p, "&eOnline Alts&7: &b" + alts.toString().replace("[", "").replace("]", "").replace(",", "&7,&r"));
        } else {
            MessageUtil.sendMessage(p, "&eOnline Alts&7: &bnone");
        }
    }

    public static String readableTime(long time) {
        int SECOND = 1000;
        int MINUTE = 60 * SECOND;
        int HOUR = 60 * MINUTE;
        int DAY = 24 * HOUR;

        long ms = time;
        StringBuffer text = new StringBuffer("");
        if (ms > DAY) {
            text.append(ms / DAY).append(" days ");
            ms %= DAY;
        }
        if (ms > HOUR) {
            text.append(ms / HOUR).append(" hours ");
            ms %= HOUR;
        }
        if (ms > MINUTE) {
            text.append(ms / MINUTE).append(" minutes ");
            ms %= MINUTE;
        }
        if (ms > SECOND) {
            text.append(ms / SECOND).append(" seconds ");
            ms %= SECOND;
        }
        text.append(ms);
        return text.toString();
    }
}
