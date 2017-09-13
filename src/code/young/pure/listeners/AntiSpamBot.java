package code.young.pure.listeners;

import code.young.pure.Pure;
import gnu.trove.TCollections;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;

/*
 * Created by Calvin on 2/17/2015.
 * Copyright 2015, Sairex Media, All rights reserved.
 */

public class AntiSpamBot implements Listener {

    private int limit = Pure.getInstance().getConfiguration().getConfig().getInt("anti-spambot.account-limit");
    private TObjectIntMap<InetAddress> addresses = TCollections.synchronizedMap(new TObjectIntHashMap<InetAddress>());

    @EventHandler
    public void login(LoginEvent e) {
        if (addresses.get(e.getConnection().getAddress().getAddress()) >= limit) {
            e.setCancelReason(ChatColor.RED + "You already have " + limit + " accounts using this IP Address.\nPlease try logging one off them to log in.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void postLogin(PostLoginEvent e) {
        addresses.adjustOrPutValue(e.getPlayer().getAddress().getAddress(), 1, 1);
    }

    @EventHandler
    public void disconnect(PlayerDisconnectEvent e) {
        InetAddress addr = e.getPlayer().getAddress().getAddress();
        addresses.adjustValue(addr, -1);
        if (addresses.get(addr) <= 0) {
            addresses.remove(addr);
        }
    }
}