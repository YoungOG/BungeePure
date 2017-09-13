package code.young.pure.commands;


import code.young.pure.Pure;
import code.young.pure.database.DatabaseManager;
import code.young.pure.management.ProfileManager;
import code.young.pure.objects.Profile;
import code.young.pure.utils.IPUtils;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.PlayerCommand;

import java.util.ArrayList;

public class ProfileCommand extends PlayerCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();
    private DatabaseManager dm = Pure.getInstance().getDatabaseManager();

    public ProfileCommand() {
        super("profile", "pure.profile", "prof");
    }

    public void execute(final CommandSender p, String[] args) {
        if (args.length == 0) {
            if (!(p instanceof ProxiedPlayer)) {
                return;
            }

            ProxiedPlayer pp = (ProxiedPlayer) p;

            pm.getProfile(pp.getUniqueId()).information(pp);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("info")) {
                MessageUtil.sendMessage(p, "&eThere are currently &7(&b" + dm.getProfileCollection().count() + "&7) &eprofiles on the network.");
                MessageUtil.sendMessage(p, "&eThere are currently &7(&b" + Pure.getInstance().getProfileManager().getProfiles().size() + "&7) &eloaded profiles on the network.");
                int totallogins = 0;
                for (Profile profs : pm.getProfiles()) {
                    totallogins += profs.getLogins();
                }
                MessageUtil.sendMessage(p, "&eThere has been a total of &7(&b" + totallogins + "&7) &elogins on the network.");
            } else {
                if (IPUtils.isValidIP(args[0])) {
                    int count = 0;
                    ArrayList<Profile> foundProfs = new ArrayList<>();

                    for (Profile profs : pm.getProfiles()) {
                        if (profs.getIps().contains(args[0])) {
                            count++;
                            foundProfs.add(profs);
                        }
                    }

                    if (count > 0) {
                        for (Profile tProfs : foundProfs) {
                            TextComponent m2 = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&b" + tProfs.getCurrentName()));
                            m2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                    ChatColor.translateAlternateColorCodes('&', "&eCurrent Name&7: &b" + tProfs.getCurrentName()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eAddress&7: &b" + tProfs.getCurrentIP()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eDate Created&7: &b" + tProfs.getCreatedDate()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eRecent Names&7: " + (tProfs.getRecentNames().size() > 0 ? "(&b" + tProfs.getRecentNames().size() + "&7): &b" + tProfs.getRecentNames().toString().replace("[", "").replace("]", "") : "(&b0&7)")) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eKnown Alts&7: " + (tProfs.getAlts().size() > 0 ? "(&b" + tProfs.getAlts().size() + "&7): &b" + tProfs.getAlts().toString().replace("[", "").replace("]", "") : "(&b0&7)")) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eKnown IPs&7: " + (tProfs.getIps().size() > 0 ? "(&b" + tProfs.getIps().size() + "&7): &b" + tProfs.getIps().toString().replace("[", "").replace("]", "") : "(&b0&7)")) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eCurrent Server&7: &b" + tProfs.getCurrentServer()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eRecent Server&7: &b" + tProfs.getRecentServer()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eOnline&7: &b" + tProfs.isOnline()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eRegistered&7: &b" + tProfs.isRegistered()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eTotal Logins&7: &b" + tProfs.getLogins()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eTemp Banned&7: &b" + tProfs.isTempBanned()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&ePerm Banned&7: &b" + tProfs.isPermBanned()) +
                                            ChatColor.translateAlternateColorCodes('&', "\n&eTotal Bans&7: &b" + tProfs.getBans())).create()));
                            TextComponent m1 = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eProfile&7: "));
                            m1.addExtra(m2);
                            MessageUtil.sendMessage(p, m1);
                        }
                        MessageUtil.sendMessage(p, "&eFound &7(&b" + count + "&7) &eprofiles that have used the IP Address &7(&b" + args[0] + "&&)&e.");
                        MessageUtil.sendMessage(p, "&eNote&7: &eHover over the profiles to view information.");
                    } else {
                        MessageUtil.sendMessage(p, "&cError: No profiles could be found under that IP Address.");
                    }
                } else {
                    if (BungeeCord.getInstance().getPlayer(args[0]) != null) {
                        ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);

                        Profile prof = pm.getProfile(target.getUniqueId());
                        prof.information(p);
                    } else {
                        Profile prof = pm.getProfile(args[0]);
                        if (prof != null) {
                            prof.information(p);
                        } else {
                            MessageUtil.sendMessage(p, "&cError: Could not find a profile for: " + args[0]);
                        }
                    }
                }
            }
        }
    }
}
