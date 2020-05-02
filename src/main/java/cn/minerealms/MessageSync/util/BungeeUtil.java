package cn.minerealms.MessageSync.util;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cn.minerealms.MessageSync.MessageSync;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;



public class BungeeUtil {
    static int a = 0;
    public static void sendbcServerChat(MessageSync plugin, String msg) {
        try {
            String servername = plugin.getConfig().getString("ServerName", "Why?");
//			plugin.sendServerChat(servername, msg);
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF("ServerChat");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            //
            msgout.writeLong(System.currentTimeMillis());
            //
            msgout.writeUTF(servername);
            msgout.writeUTF(msg);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
