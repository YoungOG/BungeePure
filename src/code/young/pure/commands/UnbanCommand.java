package code.young.pure.commands;

import code.young.pure.management.BanManager;
import code.young.pure.Pure;
import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UnbanCommand extends Command {

    BanManager bm = Pure.getInstance().getBanManager();

    public UnbanCommand() {
        super("unban", "pure.ban", "pardon", "eunban");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            MessageUtil.sendMessage(sender, "&cUsage: /unban <name>");
            return;
        }

        bm.unban(sender, args[0]);
    }
}
