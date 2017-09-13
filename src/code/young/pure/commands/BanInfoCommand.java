package code.young.pure.commands;

import code.young.pure.Pure;
import code.young.pure.management.BanManager;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BanInfoCommand extends Command {

    BanManager bm = Pure.getInstance().getBanManager();

    public BanInfoCommand() {
        super("baninfo", "pure.baninfo", "bi", "checkstaff");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            MessageUtil.sendMessage(sender, "&cUsage: /baninfo <name>");
            return;
        }

        if (args[0].equalsIgnoreCase("count")) {
            bm.getBanCount(sender);
        } else {
            bm.checkStaff(sender, args[0]);
        }
    }
}
