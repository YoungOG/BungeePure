package code.young.pure.commands;

import code.young.pure.Pure;
import code.young.pure.management.BanManager;
import code.young.pure.utils.IPUtils;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class TempBanIPCommand extends Command {

    BanManager bm = Pure.getInstance().getBanManager();

    public TempBanIPCommand() {
        super("tempbanip", "pure.tempbanip", "etempbanip");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "&cUsage: /tempbanip <name> <time> <reason>");
            return;
        }

        StringBuilder buffer = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            buffer.append(' ').append(args[i]);
        }

        String ip = args[0];
        String time = args[1];

        if (IPUtils.isValidIP(ip)) {
            bm.tempbanAddress(ip, sender, time, buffer.toString());
        } else {
            MessageUtil.sendMessage(sender, "&cError: " + ip + " is not a valid IP Address.");
            return;
        }
    }
}
