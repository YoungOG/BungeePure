package code.young.pure.commands;


import code.young.pure.Pure;
import code.young.pure.management.ProfileManager;
import code.young.pure.objects.Profile;
import code.young.pure.utils.DateUtil;
import code.young.pure.utils.IPUtils;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.PlayerCommand;

import java.util.ArrayList;

public class SeenCommand extends PlayerCommand {

    private ProfileManager pm = Pure.getInstance().getProfileManager();

    public SeenCommand() {
        super("seen", "pure.seen", "whois", "checkban", "cb");
    }

    public void execute(final CommandSender p, String[] args) {
        if (args.length == 0) {
            if (!(p instanceof ProxiedPlayer)) {
                return;
            }

            ProxiedPlayer pp = (ProxiedPlayer) p;

            pm.getProfile(pp.getUniqueId()).seen(pp);
        } else if (args.length == 1) {
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
                        TextComponent m2 = new TextComponent("&b" + tProfs.getCurrentName());
                        m2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                        "&eCurrent Name&7: &b" + tProfs.getCurrentName() +
                                        "\n&eAddress&7: &b" + tProfs.getCurrentIP() +
                                        "\n&eDate Created&7: &b" + tProfs.getCreatedDate() +
                                        "\n&eRecent Names &7(&b" + tProfs.getRecentNames().size() + "&e): &b" + tProfs.getRecentNames().toString().replace("[", "").replace("]", "") +
                                        "\n&eKnown Alts &7(&b" + tProfs.getAlts().size() + "&e): &b" + tProfs.getAlts().toString().replace("[", "").replace("]", "") +
                                        "\n&eKnown IPs &7(&b" + tProfs.getIps().size() + "&e): &b" + tProfs.getIps().toString().replace("[", "").replace("]", "") +
                                        "\n&eCurrent Server&7: &b" + tProfs.getCurrentServer() +
                                        "\n&eRecent Server&7: &b" + tProfs.getRecentServer() +
                                        "\n&eOnline&7: &b" + tProfs.isOnline() +
                                        "\n&eRegistered&7: &b" + tProfs.isRegistered() +
                                        "\n&eTotal Logins&7: &b" + tProfs.getLogins() +
                                        "\n&eTotal Playtime&7: &b" + DateUtil.formatDateDiff((System.currentTimeMillis() - tProfs.getLastLogin() + tProfs.getTotalTimeOnline())) +
                                        "\n&eTemp Banned&7: &b" + tProfs.isTempBanned() +
                                        "\n&ePerm Banned&7: &b" + tProfs.isPermBanned() +
                                        "\n&eTotal Bans&7: &b" + tProfs.getBans()).create()));
                        TextComponent m1 = new TextComponent("&eProfile: ");
                        m1.addExtra(m2);
                        MessageUtil.sendMessage(p, m1);
                    }
                    MessageUtil.sendMessage(p, "&eFound &7(&b" + count + "&7) &eprofiles that have used the IP Address &7(&b" + args[0] + "&&)&e.");
                    MessageUtil.sendMessage(p, "&eNote: Hover over the profiles to view information.");
                } else {
                    MessageUtil.sendMessage(p, "&cError: No profiles could be found under that IP Address.");
                }
            } else {
                Profile prof = pm.getProfile(args[0]);

                if (prof != null) {
                    prof.seen(p);
                } else {
                    MessageUtil.sendMessage(p, "&cError: Could not find a profile for: " + args[0]);
                }
            }
        }
    }
}
