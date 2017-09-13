package code.young.pure.management;

import code.young.pure.Pure;
import code.young.pure.database.DatabaseManager;
import code.young.pure.objects.*;
import code.young.pure.utils.DateUtil;
import code.young.pure.utils.MessageUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.StringEscapeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

public class BanManager {

    private Pure instance = Pure.getInstance();
    private ProfileManager pm = instance.getProfileManager();
    private DatabaseManager dm = instance.getDatabaseManager();
    private ArrayList<Ban> bans = new ArrayList<>();
    private ArrayList<TempBan> tempbans = new ArrayList<>();
    private ArrayList<IPBan> ipbans = new ArrayList<>();
    private ArrayList<TempIPBan> tempipbans = new ArrayList<>();

    public void loadBans() {
        DBCursor dbc1 = dm.getPermBans().find();
        DBCursor dbc2 = dm.getTempBans().find();
        DBCursor dbc3 = dm.getPermIPBans().find();
        DBCursor dbc4 = dm.getTempIPBans().find();

        while (dbc1.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc1.next();

            UUID id = UUID.fromString(dbo.getString("uuid"));

            Ban b = new Ban(id, dbo.getString("banner"), dbo.getString("reason"), dbo.getString("date"));

            bans.add(b);
        }

        while (dbc2.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc2.next();

            UUID id = UUID.fromString(dbo.getString("uuid"));

            TempBan b = new TempBan(id, dbo.getString("banner"), dbo.getString("reason"), dbo.getLong("banned_until"), dbo.getString("date"));

            tempbans.add(b);
        }

        while (dbc3.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc3.next();

            IPBan b = new IPBan(dbo.getString("address"), dbo.getString("banner"), dbo.getString("reason"), dbo.getString("date"));

            ipbans.add(b);
        }

        while (dbc4.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc4.next();

            TempIPBan b = new TempIPBan(dbo.getString("address"), dbo.getString("banner"), dbo.getString("reason"), dbo.getLong("banned_until"), dbo.getString("date"));

            tempipbans.add(b);
        }

        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eSuccessfully loaded &7(&b" + bans.size() + "&7) &ePermanent Bans."));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eSuccessfully loaded &7(&b" + tempbans.size() + "&7) &eTemporary Bans."));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eSuccessfully loaded &7(&b" + ipbans.size() + "&7) &ePermanent IPBans."));
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &eSuccessfully loaded &7(&b" + tempipbans.size() + "&7) &eTemporary IPBans."));

        instance.setLocked(false);
        BungeeCord.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', "&7[&b&lPure&7]: &aThe Network lock has been disabled."));
    }

    public void banName(String name, CommandSender banner, String reason) {
        if (pm.getProfile(name) == null) {
            MessageUtil.sendMessage(banner, "&cError: Could not find a profile for that player.");
            return;
        }

        Profile prof = pm.getProfile(name);

        if (isPermBanned(prof.getUniqueId())) {
            MessageUtil.sendMessage(banner, "&cError: That player is already banned.");
            return;
        }

        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date dateobj = new Date();

        prof.setPermBanned(true);
        prof.setOnline(false);
        prof.setLastLogout(System.currentTimeMillis());
        prof.setBans(prof.getBans() + 1);
        pm.reloadProfile(prof);

        BasicDBObject dbo = new BasicDBObject()
                .append("uuid", prof.getUniqueId().toString())
                .append("banner", banner.getName())
                .append("reason", reason)
                .append("date", df.format(dateobj));
        dm.getPermBans().insert(dbo);

        Ban b = new Ban(prof.getUniqueId(), banner.getName(), reason, df.format((dateobj)));
        getPermanentBans().add(b);

        for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
            if (all.hasPermission("pure.ban.view")) {
                MessageUtil.sendMessage(all, "&b" + prof.getCurrentName() + " &ehas been banned by &4" + banner.getName() + "&e.");
                MessageUtil.sendMessage(all, "&eReason&7: &b" + reason);
            } else {
                if (all.getUniqueId() != prof.getUniqueId()) {
                    MessageUtil.sendMessage(all, "&b" + prof.getCurrentName() + " &ehas been banned by &4" + banner.getName() + "&e.");
                }
            }
        }
    }

    public void banPlayer(ProxiedPlayer banned, CommandSender banner, String reason) {
        if (pm.getProfile(banned.getUniqueId()) == null) {
            MessageUtil.sendMessage(banner, "&cError: Could not find a profile for that player.");
            return;
        }

        if (isPermBanned(banned.getUniqueId())) {
            MessageUtil.sendMessage(banner, "&cError: That player is already banned.");
            return;
        }

        Profile prof = pm.getProfile(banned.getUniqueId());
        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date dateobj = new Date();

        prof.setPermBanned(true);
        prof.setOnline(false);
        prof.setLastLogout(System.currentTimeMillis());
        prof.setBans(prof.getBans() + 1);
        pm.reloadProfile(prof);

        BasicDBObject dbo = new BasicDBObject()
                .append("uuid", prof.getUniqueId().toString())
                .append("banner", banner.getName())
                .append("reason", reason)
                .append("date", df.format(dateobj));
        dm.getPermBans().insert(dbo);

        Ban b = new Ban(prof.getUniqueId(), banner.getName(), reason, df.format((dateobj)));
        getPermanentBans().add(b);

        banned.disconnect(ChatColor.translateAlternateColorCodes('&', "&cYour account has been suspended from the " + MessageUtil.getNetworkName() + ".\n \n&cVisit " + MessageUtil.getWebstore() + " to purchase an unban or submit an appeal."));

        for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
            if (all.hasPermission("pure.ban.view")) {
                MessageUtil.sendMessage(all, "&b" + prof.getCurrentName() + " &ehas been banned by &4" + banner.getName() + "&e.");
                MessageUtil.sendMessage(all, "&eReason&7: &b" + reason);
            } else {
                if (all.getUniqueId() != prof.getUniqueId()) {
                    MessageUtil.sendMessage(all, "&b" + prof.getCurrentName() + " &ehas been banned by &4" + banner.getName() + "&e.");
                }
            }
        }
    }

    public void tempBan(ProxiedPlayer banned, CommandSender banner, String time, String reason) {
        if (pm.getProfile(banned.getUniqueId()) == null) {
            MessageUtil.sendMessage(banner, "&cError: A profile could not be found for that player.");
            return;
        }

        Profile prof = pm.getProfile(banned.getUniqueId());

        if (isTempBanned(prof.getUniqueId())) {
            MessageUtil.sendMessage(banner, "&cError: That player is already banned.");
            return;
        }

        try {
            long bannedUntil = DateUtil.parseDateDiff(time, true);
            DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            Date dateobj = new Date();

            prof.setTempBanned(true);
            prof.setOnline(false);
            prof.setLastLogout(System.currentTimeMillis());
            prof.setBans(prof.getBans() + 1);
            pm.reloadProfile(prof);

            BasicDBObject dbo = new BasicDBObject()
                    .append("uuid", prof.getUniqueId().toString())
                    .append("banner", banner.getName())
                    .append("reason", reason)
                    .append("banned_until", bannedUntil)
                    .append("date", df.format(dateobj));
            dm.getTempBans().insert(dbo);

            TempBan b = new TempBan(prof.getUniqueId(), banner.getName(), reason, bannedUntil, df.format((dateobj)));
            getTemporaryBans().add(b);

            banned.disconnect(ChatColor.translateAlternateColorCodes('&', "&cYour account has been suspended from the " + MessageUtil.getNetworkName() + ".\n \n&cVisit " + MessageUtil.getWebstore() + " to purchase an unban or submit an appeal."));

            for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
                if (all.hasPermission("pure.ban.view")) {
                    MessageUtil.sendMessage(all, "&b" + prof.getCurrentName() + " &ehas been temporarily banned by &4" + banner.getName() + "&e.");
                    MessageUtil.sendMessage(all, "&eReason&7: &b" + reason);
                    MessageUtil.sendMessage(all, "&eLenght&7: &b" + DateUtil.formatDateDiff(bannedUntil));
                } else {
                    if (all.getUniqueId() != prof.getUniqueId()) {
                        MessageUtil.sendMessage(all, "&b" + prof.getCurrentName() + " &ehas been temporarily banned by &4" + banner.getName() + "&e.");
                    }
                }
            }
        } catch (Exception ex) {
            MessageUtil.sendMessage(banner, "&cError: The time you entered is not valid.");
        }
    }

    public void tempBanName(String name, CommandSender banner, String time, String reason) {
        if (pm.getProfile(name) == null) {
            MessageUtil.sendMessage(banner, "&cError: A profile could not be found for that player.");
            return;
        }

        Profile prof = pm.getProfile(name);

        if (isTempBanned(prof.getUniqueId())) {
            MessageUtil.sendMessage(banner, "&cError: That player is already banned.");
            return;
        }

        try {
            long bannedUntil = DateUtil.parseDateDiff(time, true);

            DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            Date dateobj = new Date();

            prof.setTempBanned(true);
            prof.setOnline(false);
            prof.setLastLogout(System.currentTimeMillis());
            prof.setBans(prof.getBans() + 1);
            pm.reloadProfile(prof);

            BasicDBObject dbo = new BasicDBObject()
                    .append("uuid", prof.getUniqueId().toString())
                    .append("banner", banner.getName())
                    .append("reason", reason)
                    .append("banned_until", bannedUntil)
                    .append("date", df.format(dateobj));
            dm.getTempBans().insert(dbo);

            TempBan b = new TempBan(prof.getUniqueId(), banner.getName(), reason, bannedUntil, df.format((dateobj)));
            getTemporaryBans().add(b);

            for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
                if (all.hasPermission("pure.ban.view")) {
                    MessageUtil.sendMessage(all, "&b" + prof.getCurrentName() + " &ehas been temporarily banned by &4" + banner.getName() + "&e.");
                    MessageUtil.sendMessage(all, "&eReason&7: &b" + reason);
                    MessageUtil.sendMessage(all, "&eLenght&7: &b" + DateUtil.formatDateDiff(bannedUntil));
                } else {
                    if (all.getUniqueId() != prof.getUniqueId()) {
                        MessageUtil.sendMessage(all, "&b" + prof.getCurrentName() + " &ehas been temporarily banned by &4" + banner.getName() + "&e.");
                    }
                }
            }
        } catch (Exception ex) {
            MessageUtil.sendMessage(banner, "&cError: The time you entered is not valid.");
        }
    }

    public void banAddress(String address, CommandSender banner, String reason) {
        if (isPermIPBanned(address)) {
            MessageUtil.sendMessage(banner, "&cError: That IP Address is already banned.");
            return;
        }

        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date dateobj = new Date();

        BasicDBObject dbo = new BasicDBObject()
                .append("address", StringEscapeUtils.escapeJson(address))
                .append("banner", banner.getName())
                .append("reason", reason)
                .append("date", df.format(dateobj));
        dm.getPermIPBans().insert(dbo);

        IPBan b = new IPBan(address, banner.getName(), reason, df.format((dateobj)));
        getPermanentIPBans().add(b);

        for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
            if (all.getAddress().getAddress().getHostAddress().replace("/", "").contains(address) || all.getAddress().getAddress().getHostAddress().replace("/", "").equalsIgnoreCase(address)) {
                all.disconnect(ChatColor.translateAlternateColorCodes('&', "&cThis IP Address has been suspended from the " + MessageUtil.getNetworkName() + ".\n \n&cVisit " + MessageUtil.getWebstore() + " to purchase an unban or submit an appeal."));
            }
            if (all.hasPermission("pure.ban.view")) {
                MessageUtil.sendMessage(all, "&b" + address + " &ehas been temporarily banned by &4" + banner.getName() + "&e.");
                MessageUtil.sendMessage(all, "&eReason&7: &b" + reason);
            }
        }
    }

    public void tempbanAddress(String address, CommandSender banner, String time, String reason) {
        if (isPermIPBanned(address)) {
            MessageUtil.sendMessage(banner, "&cError: That IP Address is already banned.");
            return;
        }

        try {
            long bannedUntil = DateUtil.parseDateDiff(time, true);
            DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            Date dateobj = new Date();

            BasicDBObject dbo = new BasicDBObject()
                    .append("address", StringEscapeUtils.escapeJson(address))
                    .append("banner", banner.getName())
                    .append("reason", reason)
                    .append("banned_until", bannedUntil)
                    .append("date", df.format(dateobj));
            dm.getTempIPBans().insert(dbo);

            TempIPBan b = new TempIPBan(address, banner.getName(), reason, bannedUntil, df.format((dateobj)));
            getTemporaryIPBans().add(b);

            for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
                if (all.getAddress().getAddress().getHostAddress().replace("/", "").contains(address) || all.getAddress().getAddress().getHostAddress().replace("/", "").equalsIgnoreCase(address)) {
                    all.disconnect(ChatColor.translateAlternateColorCodes('&', "&cThis IP Address has been suspended from the " + MessageUtil.getNetworkName() + ".\n \n&cVisit " + MessageUtil.getWebstore() + " to purchase an unban or submit an appeal."));
                }
                if (all.hasPermission("pure.ban.view")) {
                    MessageUtil.sendMessage(all, "&b" + address + " &ehas been temporarily banned by &4" + banner.getName() + "&e.");
                    MessageUtil.sendMessage(all, "&eReason&7: &b" + reason);
                    MessageUtil.sendMessage(all, "&eLenght&7: &b" + DateUtil.formatDateDiff(bannedUntil));
                }
            }
        } catch (Exception ex) {
            MessageUtil.sendMessage(banner, "&cError: The time you entered is not valid.");
        }
    }

    public void unban(CommandSender unbanner, String name) {
        if (pm.getProfile(name) == null) {
            MessageUtil.sendMessage(unbanner, "&cError: A profile for that player could not be found.");
            return;
        }

        Profile prof = pm.getProfile(name);

        if (!isTempBanned(prof.getUniqueId()) && !isPermBanned(prof.getUniqueId())) {
            MessageUtil.sendMessage(unbanner, "&cError: That player is not currently banned.");
            return;
        }

        prof.setPermBanned(false);
        prof.setTempBanned(false);

        if (isPermBanned(prof.getUniqueId())) {
            DBCursor dbc = dm.getPermBans().find(new BasicDBObject("uuid", prof.getUniqueId().toString()));

            BasicDBObject dbo = (BasicDBObject) dbc.next();

            Ban ban = null;

            for (Ban b : getPermanentBans()) {
                if (b.getBannedUUID().equals(UUID.fromString(dbo.getString("uuid")))) {
                    ban = b;
                }
            }

            getPermanentBans().remove(ban);
            dm.getPermBans().remove(dbo);
        }

        if (isTempBanned(prof.getUniqueId())) {
            DBCursor dbc = dm.getTempBans().find(new BasicDBObject("uuid", prof.getUniqueId().toString()));
            BasicDBObject dbo = (BasicDBObject) dbc.next();
            TempBan tb = null;

            for (TempBan b : getTemporaryBans()) {
                if (b.getBannedUUID().equals(UUID.fromString(dbo.getString("uuid")))) {
                    tb = b;
                }
            }

            getTemporaryBans().remove(tb);
            dm.getTempBans().remove(dbo);
        }

        for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
            if (all.hasPermission("pure.unban.view")) {
                MessageUtil.sendMessage(all, "&4" + unbanner.getName() + " &ehas unbanned&7: &b" + name);
            }
        }

        pm.reloadProfile(prof);
    }

    public void unbanAddress(CommandSender unbanner, String address) {
        if (!isPermIPBanned(address) && !isTempIPBanned(address)) {
            MessageUtil.sendMessage(unbanner, "&cError: That IP Address is not currently banned.");
            return;
        }

        if (isPermIPBanned(address)) {
            DBCursor dbc = dm.getPermIPBans().find(new BasicDBObject("address", StringEscapeUtils.escapeJson(address)));
            BasicDBObject dbo = (BasicDBObject) dbc.next();
            IPBan ipb = null;

            for (IPBan b : getPermanentIPBans()) {
                if (address.equals(dbo.getString("address"))) {
                    ipb = b;
                }
            }

            getPermanentIPBans().remove(ipb);
            dm.getPermIPBans().remove(dbo);
        }

        if (isTempIPBanned(address)) {
            DBCursor dbc = dm.getTempIPBans().find(new BasicDBObject("address", StringEscapeUtils.escapeJson(address)));
            BasicDBObject dbo = (BasicDBObject) dbc.next();
            TempIPBan tipb = null;

            for (TempIPBan b : getTemporaryIPBans()) {
                if (address.equals(dbo.getString("address"))) {
                    tipb = b;
                }
            }

            getTemporaryIPBans().remove(tipb);
            dm.getTempIPBans().remove(dbo);
        }

        for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
            if (all.hasPermission("pure.unban.view")) {
                MessageUtil.sendMessage(all, "&4" + unbanner.getName() + " &ehas unbanned&7: &b" + address);
            }
        }
    }

    public Boolean isPermBanned(UUID id) {
        return dm.getPermBans().find(new BasicDBObject("uuid", id.toString())).hasNext();
    }

    public Boolean isTempBanned(UUID id) {
        return dm.getTempBans().find(new BasicDBObject("uuid", id.toString())).hasNext();
    }

    public Boolean isPermIPBanned(String address) {
        return dm.getPermIPBans().find(new BasicDBObject("address", StringEscapeUtils.escapeJson(address))).hasNext();
    }

    public Boolean isTempIPBanned(String address) {
        return dm.getTempIPBans().find(new BasicDBObject("address", StringEscapeUtils.escapeJson(address))).hasNext();
    }

    public void checkStaff(CommandSender p, String name) {
        if (pm.getProfile(name) == null) {
            MessageUtil.sendMessage(p, "&cError: That is not a valid player.");
            return;
        }

        MessageUtil.sendMessage(p, "&eLooking for all bans issued by&7: &b" + name + "&e...");

        int bc = 0;

        ArrayList<Ban> blist = new ArrayList<>();
        ArrayList<TempBan> tblist = new ArrayList<>();
        ArrayList<IPBan> iblist = new ArrayList<>();
        ArrayList<TempIPBan> tiblist = new ArrayList<>();

        for (Ban b : getPermanentBans()) {
            if (b.getBanner().equalsIgnoreCase(name)) {
                bc++;
                blist.add(b);
            }
        }

        for (TempBan b : getTemporaryBans()) {
            if (b.getBanner().equalsIgnoreCase(name)) {
                bc++;
                tblist.add(b);
            }
        }

        for (IPBan b : getPermanentIPBans()) {
            if (b.getBanner().equalsIgnoreCase(name)) {
                bc++;
                iblist.add(b);
            }
        }

        for (TempIPBan b : getTemporaryIPBans()) {
            if (b.getBanner().equalsIgnoreCase(name)) {
                bc++;
                tiblist.add(b);
            }
        }

        if (bc == 0) {
            MessageUtil.sendMessage(p, "&cError: This player has not issued any bans.");
            return;
        }

        for (Ban b : blist) {
            Profile prof = pm.getProfile(b.getBannedUUID());

            TextComponent m = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eName&7: &b" + prof.getCurrentName() + " &eType&7: &bPermanent"));
            m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&', "&eName&7: &b" + prof.getCurrentName()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eBanned By&7: &b" + b.getBanner()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eReason&7: &b" + b.getReason()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eIssued&7: &b" + b.getDate())).create()));
            MessageUtil.sendMessage(p, m);
        }

        for (TempBan b : tblist) {
            Profile prof = pm.getProfile(b.getBannedUUID());

            TextComponent m = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eName: &b" + prof.getCurrentName() + " &eType&7: &bTemporary"));
            m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&', "&eName&7: &b" + prof.getCurrentName()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eBanned By&7: &b" + b.getBanner()) +
                           ChatColor.translateAlternateColorCodes('&', "\n&eReason&7: &b" + b.getReason()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eBan Lenght&7: &b" + DateUtil.formatDateDiff(b.getBannedUntil())) +
                                    ChatColor.translateAlternateColorCodes('&', "\n&eIssued&7: &b" + b.getDate())).create()));
            MessageUtil.sendMessage(p, m);
        }

        for (IPBan b : iblist) {
            TextComponent m = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eAddress: &b" + b.getBannedAddress() + " &eType&7: &bPermanent"));
            m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&', "&eAddress: &b" + b.getBannedAddress()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eBanned By: &b" + b.getBanner()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eReason: &b" + b.getReason()) +
                                    ChatColor.translateAlternateColorCodes('&', "\n&eIssued: &b" + b.getDate())).create()));
            MessageUtil.sendMessage(p, m);
        }

        for (TempIPBan b : tiblist) {
            TextComponent m = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eAddress: &b" + b.getAddress() + " &eType&7: &bTemporary"));
            m.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&', "&eAddress: &b" + b.getAddress()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eBanned By: &b" + b.getBanner()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eReason: &b" + b.getReason()) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eBan Lenght: &b" + DateUtil.formatDateDiff(b.getBannedUntil())) +
                            ChatColor.translateAlternateColorCodes('&', "\n&eIssued: &b" + b.getDate())).create()));
            MessageUtil.sendMessage(p, m);
        }

        MessageUtil.sendMessage(p, "&eNote: Hover over the ban to view more information.");
        MessageUtil.sendMessage(p, "&eFound a total of &7(&b" + bc + "&7) &ebans issued by this player.");
    }

    public void getBanCount(CommandSender p) {
        MessageUtil.sendMessage(p, "&eThere are a total of &7(&b" + (getPermanentBans().size() + getTemporaryBans().size() + getPermanentIPBans().size() + getTemporaryIPBans().size()) + "&7) &ebans on the network.");
        MessageUtil.sendMessage(p, "&ePermanent&7: &b" + getPermanentBans().size());
        MessageUtil.sendMessage(p, "&eTemporary&7: &b" + getTemporaryBans().size());
        MessageUtil.sendMessage(p, "&ePermanent IP&7: &b" + getPermanentIPBans().size());
        MessageUtil.sendMessage(p, "&eTemporary IP&7: &b" + getTemporaryIPBans().size());
    }

    public ArrayList<Ban> getPermanentBans() {
        return bans;
    }

    public ArrayList<TempBan> getTemporaryBans() {
        return tempbans;
    }

    public ArrayList<IPBan> getPermanentIPBans() {
        return ipbans;
    }

    public ArrayList<TempIPBan> getTemporaryIPBans() {
        return tempipbans;
    }
}