package code.young.pure.commands;

import code.young.pure.Pure;
import code.young.pure.management.ProfileManager;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {

    ProfileManager pm = Pure.getInstance().getProfileManager();

    public HubCommand() {
        super("hub", "pure.hub", new String[0]);
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (!p.getServer().getInfo().equals(BungeeCord.getInstance().getServerInfo("Hub"))) {
            MessageUtil.sendMessage(p, "&aSending you to the &bHub&a...");
            p.connect(BungeeCord.getInstance().getServerInfo("Hub"));
        }
    }
}
