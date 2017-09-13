package code.young.pure.commands;

import code.young.pure.Pure;
import code.young.pure.management.BanManager;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BanCommand extends Command {

    BanManager bm = Pure.getInstance().getBanManager();

    public BanCommand() {
        super("ban", "pure.ban", "eban");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "&cUsage: /ban <name> <reason>");
            return;
        }

        StringBuilder buffer = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            buffer.append(' ').append(args[i]);
        }

        if (BungeeCord.getInstance().getPlayer(args[0]) != null) {
            ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
            bm.banPlayer(target, sender, buffer.toString());
        } else {
            bm.banName(args[0], sender, buffer.toString());
        }
    }
}
