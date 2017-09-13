package code.young.pure.listeners;

import code.young.pure.database.DatabaseManager;
import code.young.pure.management.BanManager;
import code.young.pure.management.ProfileManager;
import code.young.pure.objects.Profile;
import code.young.pure.Pure;
import code.young.pure.utils.DateUtil;
import code.young.pure.utils.MessageUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.concurrent.TimeUnit;

public class LoginListener implements Listener {

    private Pure instance = Pure.getInstance();
    private ProfileManager pm = instance.getProfileManager();
    private BanManager bm = instance.getBanManager();
    private DatabaseManager dm = instance.getDatabaseManager();

    @EventHandler
    public void onPreLogin(final LoginEvent e) {
        if (instance.isLocked()) {
            e.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&cNetwork is still loading.\nPlease try again in a few moments."));
            e.setCancelled(true);
        }

        String ip = e.getConnection().getAddress().getAddress().getHostAddress().replace("/", "");

        if (bm.isPermIPBanned(ip)) {
            e.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&cThis IP Address has been suspended from the " + MessageUtil.getNetworkName() + "\n \n & cVisit " + MessageUtil.getWebstore() + " to purchase an unban or submit an appeal."));
                    e.setCancelled(true);
            return;
        }

        if (bm.isTempIPBanned(ip)) {
            DBCursor dbc = dm.getTempIPBans().find(new BasicDBObject("address", StringEscapeUtils.escapeJson(ip)));

            if (dbc.hasNext()) {
                BasicDBObject dbo = (BasicDBObject) dbc.next();
                if (System.currentTimeMillis() > dbo.getLong("banned_until")) {
                    dm.getTempIPBans().remove(dbo);
                }

                String blah = DateUtil.formatDateDiff(dbo.getLong("banned_until"));

                e.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&cThis IP Address has been temporarily suspended from the " + MessageUtil.getNetworkName() + "\n \n&cTime remaining: &f" + blah));
                e.setCancelled(true);
                return;
            }
        }

        if (bm.isPermBanned(e.getConnection().getUniqueId())) {
            e.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&cYour account has been suspended from the " + MessageUtil.getNetworkName() + ".\n \n&cVisit " + MessageUtil.getWebstore() + " to purchase an unban or submit an appeal."));
                    e.setCancelled(true);
            return;
        }

        if (bm.isTempBanned(e.getConnection().getUniqueId())) {
            DBCursor dbc = dm.getTempBans().find(new BasicDBObject("uuid", e.getConnection().getUniqueId().toString()));

            if (dbc.hasNext()) {
                BasicDBObject dbo = (BasicDBObject) dbc.next();
                if (System.currentTimeMillis() > dbo.getLong("banned_until")) {
                    dm.getTempBans().remove(dbo);

                    Profile prof = pm.getProfile(e.getConnection().getUniqueId());
                    prof.setTempBanned(false);
                    pm.reloadProfile(prof);
                }

                String blah = DateUtil.formatDateDiff(dbo.getLong("banned_until"));

                e.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&cYour account has been temporarily suspended from the " + MessageUtil.getNetworkName() + ".\n \n&cTime Remaining: &f" + blah));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPostLogin(final PostLoginEvent e) {
        BungeeCord.getInstance().getScheduler().schedule(instance, new Runnable() {
            public void run() {
                MessageUtil.sendMessage(e.getPlayer(), "&7Loading your profile...");
                if (pm.hasProfile(e.getPlayer().getUniqueId())) {
                    pm.getProfile(e.getPlayer().getUniqueId());

                    final Profile prof = pm.getProfile(e.getPlayer().getUniqueId());

                    prof.setOnline(true);
                    prof.setLastLogin(System.currentTimeMillis());

                    if (!prof.getIps().contains(e.getPlayer().getAddress().getAddress().getHostAddress().replace("/", ""))) {
                        prof.getIps().add(e.getPlayer().getAddress().getAddress().getHostAddress().replace("/", ""));
                    }

                    for (Profile profs : pm.getProfiles()) {
                        if (profs != prof) {
                            if (profs.getIps().contains(e.getPlayer().getAddress().getAddress().getHostAddress().replace("/", ""))) {
                                if (!profs.getAlts().contains(prof.getUniqueId())) {
                                    profs.getAlts().add(prof.getUniqueId());
                                }
                                if (!prof.getAlts().contains(profs.getUniqueId())) {
                                    prof.getAlts().add(profs.getUniqueId());
                                }
                            }
                        }
                    }

                    if (!prof.getRecentNames().contains(e.getPlayer().getName())) {
                        prof.getRecentNames().add(e.getPlayer().getName());
                    }

                    if (!prof.getCurrentName().equals(e.getPlayer().getName())) {
                        prof.setCurrentName(e.getPlayer().getName());
                    }

                    prof.setLogins(prof.getLogins() + 1);

                    pm.saveProfile(prof);
                    MessageUtil.sendMessage(e.getPlayer(), "&aProfile loaded!");
                } else {
                    pm.createProfile(e.getPlayer());
                    MessageUtil.sendMessage(e.getPlayer(), "&aProfile loaded!");
                }
            }
        }, 5, TimeUnit.MILLISECONDS);
    }



//    @EventHandler
//    public void onSwitch(final ServerSwitchEvent e) {
//        BungeeCord.getInstance().getScheduler().schedule(instance, new Runnable() {
//            public void run() {
//                e.getPlayer().sendMessage("&7Loading your profile...");
//                if (pm.hasProfile(e.getPlayer())) {
//                    pm.loadProfileFromPlayer(e.getPlayer());
//
//                    final Profile prof = pm.getLoadedProfileFromProxiedPlayer(e.getPlayer());
//
//                    prof.setOnline(true);
//
//                    if (!prof.getIps().contains(e.getPlayer().getAddress().getAddress().getHostAddress().replace("/", ""))) {
//                        prof.getIps().add(e.getPlayer().getAddress().getAddress().getHostAddress().replace("/", ""));
//                    }
//
//                    if (!prof.getAlts().contains(e.getPlayer().getName())) {
//                        prof.getIps().add(e.getPlayer().getName());
//                    }
//
//                    prof.setLogins(prof.getLogins() + 1);
//
//                    pm.saveProfile(prof);
//                    e.getPlayer().sendMessage("&aProfile loaded!");
//                } else {
//                    pm.createNewProfile(e.getPlayer());
//                    e.getPlayer().sendMessage("&aProfile loaded!");
//                }
//            }
//        }, 5, TimeUnit.MILLISECONDS);
//    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        if (e.getPlayer() != null) {
            if (pm.getProfile(e.getPlayer().getUniqueId()) != null) {
                Profile prof = pm.getProfile(e.getPlayer().getUniqueId());
                if (prof.isOnline()) {
                    prof.setOnline(false);
                    prof.setLastLogout(System.currentTimeMillis());
                    if (e.getPlayer().getServer() != null) {
                        if (e.getPlayer().getServer().getInfo() != null) {
                            prof.setRecentServer(e.getPlayer().getServer().getInfo().getName());
                        } else {
                            prof.setRecentServer("none");
                        }
                    } else {
                        prof.setRecentServer("none");
                    }
                    prof.setRecentServer("none");
                }

                prof.setCurrentServer("none");
                pm.saveProfile(prof);
            }
        }
    }

    @EventHandler
    public void onKick(ServerKickEvent e) {
        if (e.getPlayer() != null) {
            if (pm.getProfile(e.getPlayer().getUniqueId()) != null) {
                Profile prof = pm.getProfile(e.getPlayer().getUniqueId());
                if (prof.isOnline()) {
                    prof.setOnline(false);
                    prof.setLastLogout(System.currentTimeMillis());
                    if (e.getPlayer().getServer() != null) {
                        if (e.getPlayer().getServer().getInfo() != null) {
                            prof.setRecentServer(e.getPlayer().getServer().getInfo().getName());
                        } else {
                            prof.setRecentServer("none");
                        }
                    } else {
                        prof.setRecentServer("none");
                    }
                    prof.setRecentServer("none");
                }
                prof.setCurrentServer("none");
                pm.saveProfile(prof);
            }
        }
    }
}