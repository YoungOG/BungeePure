package code.young.pure.commands;

import code.young.pure.utils.MessageUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class NetChatCommand extends Command {

    public NetChatCommand() {
        super("netchat", "pure.netchat", "nc");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageUtil.sendMessage(sender, "&cUsage: /nc <message>");
            return;
        }

        StringBuilder buffer = new StringBuilder();

        for (String arg : args) {
            buffer.append(' ').append(arg);
        }

        for (ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
            if (all.hasPermission("pure.netchat")) {
                if (BungeeCord.getInstance().getPlayer(sender.getName()) != null) {
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(sender.getName());
                    MessageUtil.sendMessage(all, "&e[&b&l" + p.getServer().getInfo().getName() + "&e] &4" + p.getName() + "&c:" + buffer.toString());
                } else {
                    MessageUtil.sendMessage(all, "&e[&b&lCONSOLE&e] &4Console&c:" + buffer.toString());
                }
            }
        }
    }
}
