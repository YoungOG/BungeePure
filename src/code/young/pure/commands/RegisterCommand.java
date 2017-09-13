package code.young.pure.commands;

import code.young.pure.Pure;
import code.young.pure.management.ProfileManager;
import code.young.pure.objects.Profile;
import code.young.pure.utils.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RegisterCommand extends Command {

    ProfileManager pm = Pure.getInstance().getProfileManager();
    Config cm = Pure.getInstance().getConfiguration();

    public RegisterCommand() {
        super("register", "pure.register", "reg");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

        Profile prof = pm.getProfile(p.getUniqueId());

        if (args.length != 1) {
            prof.sendMessage("&cUsage: /register [password]");
            return;
        }

        if (args[0].length() < 3) {
            prof.sendMessage("&cError: Password must be at least 3 characters long.");
            return;
        }

        if (prof.isRegistered()) {
            prof.sendMessage("&cError: You have already registered on the website.");
            return;
        }

        String password = args[0];

        prof.setPassword(password);
        prof.setRegistered(true);
        pm.reloadProfile(prof);

        prof.sendMessage("&aYour account has been successfully registered.");
        prof.sendMessage("&aVisit &b" + cm.getConfig().getString("register.register-link") + " &ato login.");
        prof.sendMessage("&aUsername&7: (&b" + prof.getCurrentName() + "&7)");
        prof.sendMessage("&aPassword&7: (&b" + prof.getPassword() + "&7)");
    }
}
