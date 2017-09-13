package code.young.pure.commands;

import code.young.pure.Pure;
import code.young.pure.management.BanManager;
import code.young.pure.utils.IPUtils;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BanIPCommand extends Command {

    BanManager bm = Pure.getInstance().getBanManager();

    public BanIPCommand() {
        super("banip", "pure.banip", "ebanip");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "&cUsage: /banip <address> <reason>");
            return;
        }

        StringBuilder buffer = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            buffer.append(' ').append(args[i]);
        }

        String ip = args[0];

        if (IPUtils.isValidIP(ip)) {
            bm.banAddress(ip, sender, buffer.toString());
        } else {
            MessageUtil.sendMessage(sender, "&cError: That is not a valid IP Address.");
            return;
        }
    }
}
