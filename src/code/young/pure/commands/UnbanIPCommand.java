package code.young.pure.commands;

import code.young.pure.Pure;
import code.young.pure.management.BanManager;
import code.young.pure.utils.IPUtils;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UnbanIPCommand extends Command {

    BanManager bm = Pure.getInstance().getBanManager();

    public UnbanIPCommand() {
        super("unbanip", "pure.unbanip", "eunbanip", "pardonip");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            MessageUtil.sendMessage(sender, "&cUsage: /unbanip <address>");
            return;
        }

        if (IPUtils.isValidIP(args[0])) {
            bm.unbanAddress(sender, args[0]);
        } else {
            MessageUtil.sendMessage(sender, "&cError: That is not a valid IP Address.");
        }
    }
}
