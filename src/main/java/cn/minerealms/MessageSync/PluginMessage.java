package cn.minerealms.MessageSync;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class PluginMessage implements PluginMessageListener {

    private MessageSync plugin;

    public PluginMessage(MessageSync plugin) {
        this.plugin = plugin;
    }

    boolean aa = false;

    @Override
    public void onPluginMessageReceived(String tag, Player player, byte[] data) {
        if (!tag.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        String channel = in.readUTF();

        if (channel.equals("ServerChat")) {

            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try {
                //
                Long time = msgin.readLong();
                if (time < System.currentTimeMillis() - 5000) {
                    return;
                }
                //
                String servername = msgin.readUTF();
                String msg = msgin.readUTF();

                if (!aa) {
                    plugin.sendServerChat(servername, msg);

                }
                if (aa) {
                    aa = false;
                }
                aa = true;

            } catch (IOException e) {
                plugin.getLogger().info("接收消息时失败");
                e.printStackTrace();
            }
        }
    }

}
