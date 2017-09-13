package code.young.pure.utils;

import code.young.pure.Pure;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Calvin on 4/8/2015 for the Zetta Network.
 * Project: Pure
 * Copyright 2015, Sairex Media, All rights reserved.
 */
public class MessageUtil {

    private Pure main = Pure.getInstance();
    private static String networkName = Pure.getInstance().getConfiguration().getConfig().getString("settings.network-name");
    private static String webstore = Pure.getInstance().getConfiguration().getConfig().getString("settings.webstore");

    public static void sendMessage(ProxiedPlayer p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(CommandSender p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(CommandSender p, TextComponent message) {
        p.sendMessage(message);
    }

    public static void sendMessage(ProxiedPlayer p, TextComponent message) {
        p.sendMessage(message);
    }

    public static void sendMessage(CommandSender p, TextComponent... tc) {
        p.sendMessage(tc);
    }

    public static void sendMessage(ProxiedPlayer p, TextComponent... tc) {
        p.sendMessage(tc);
    }

    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', Pure.getInstance().getConfiguration().getConfig().getString("settings.prefix"));
    }

    public static String getNetworkName() {
        return networkName;
    }

    public static String getWebstore() {
        return webstore;
    }
}


