package cn.minerealms.MessageSync;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.minerealms.MessageSync.util.BungeeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageSync extends JavaPlugin implements Listener {
    private static JavaPlugin Config;

    private static boolean print_unset_entity;
    private static List<String> unknow;
    private static HashMap<String, List<String>> byCauses, byEntity;

    public void loadMessages() {
        byCauses = new HashMap<>();
        byEntity = new HashMap<>();
        print_unset_entity = getSettings().getBoolean("DeathMessages.print-unset-entity", true);
        unknow = getSettings().getStringList("DeathMessages.unknow");
        getSettings().getConfigurationSection("DeathMessages.causes").getKeys(false).forEach(cause -> {
            cause = cause.toUpperCase();
            try {
                EntityDamageEvent.DamageCause.valueOf(cause);
                byCauses.put(cause, getSettings().getStringList("DeathMessages.causes." + cause));
            } catch (Exception ignored) {
            }
        });
        getSettings().getConfigurationSection("DeathMessages.entity").getKeys(false).forEach(entity -> {
            try {
                EntityType.valueOf(entity);
                byEntity.put(entity, getSettings().getStringList("DeathMessages.entity." + entity));
            } catch (Exception ignored) {
            }
        });
    }

    public static JavaPlugin get233() {
        if (Config != null) {
            return Config;
        } else {
            throw new IllegalStateException();
        }
    }

    public FileConfiguration getSettings() {
        return this.getConfig();
    }

    @Override
    public void onEnable() {
        loadMessages();
        Config = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getConsoleSender().sendMessage("§a /$$      /$$                                                           \n" +
                "| $$$    /$$$                                                           \n" +
                "| $$$$  /$$$$  /$$$$$$   /$$$$$$$ /$$$$$$$  /$$$$$$   /$$$$$$   /$$$$$$ \n" +
                "| $$ $$/$$ $$ /$$__  $$ /$$_____//$$_____/ |____  $$ /$$__  $$ /$$__  $$\n" +
                "| $$  $$$| $$| $$$$$$$$|  $$$$$$|  $$$$$$   /$$$$$$$| $$  \\ $$| $$$$$$$$\n" +
                "| $$\\  $ | $$| $$_____/ \\____  $$\\____  $$ /$$__  $$| $$  | $$| $$_____/\n" +
                "| $$ \\/  | $$|  $$$$$$$ /$$$$$$$//$$$$$$$/|  $$$$$$$|  $$$$$$$|  $$$$$$$\n" +
                "|__/     |__/ \\_______/|_______/|_______/  \\_______/ \\____  $$ \\_______/\n" +
                "                                                     /$$  \\ $$          \n" +
                "                                                    |  $$$$$$/          \n" +
                "                                                     \\______/           \n" +
                "  /$$$$$$                                                               \n" +
                " /$$__  $$                                  \n" +
                "| $$  \\__/ /$$   /$$ /$$$$$$$   /$$$$$$$                                \n" +
                "|  $$$$$$ | $$  | $$| $$__  $$ /$$_____/  \n" +
                " \\____  $$| $$  | $$| $$  \\ $$| $$   \n" +
                " /$$  \\ $$| $$  | $$| $$  | $$| $$                                      \n" +
                "|  $$$$$$/|  $$$$$$$| $$  | $$|  $$$$$$$   \n" +
                " \\______/  \\____  $$|__/  |__/ \\_______/  \n" +
                "           /$$  | $$                    \n" +
                "          |  $$$$$$/                                                    \n" +
                "           \\______/                         §b§l————Powered by MineRealms");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessage(this));
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§4BukkitDeathMessage Disable！！！");
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        Player p = e.getEntity();

        EntityDamageEvent.DamageCause cause = p.getLastDamageCause().getCause();
        String message;
        if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) p.getLastDamageCause()).getDamager();
            message = getByEntity(damager.getType()).replace("{D}", damager.getName());
        } else {
            message = getByCauses(cause);
        }
        message = message.replace("{P}", p.getName());

        //////////////////////////////////
        if (message != null) {
            message = color(message);

            try {
                e.setDeathMessage(this.getConfig().getString("ServerName", "Why?") + message);
                BungeeUtil.sendbcServerChat(this,message);
                return;
            } catch (java.lang.NullPointerException ee) {

            }
        }

//		sendDeathMessage(, p);

    }

    public static String getByCauses(EntityDamageEvent.DamageCause cause) {
        return byCauses.containsKey(cause.name()) ? getRandomFromListString(byCauses.get(cause.name())) : getUnknow();
    }

    public void sendServerChat(String servername, String msg) {
        Bukkit.broadcastMessage(servername + msg);
    }

    /**
     * 通过攻击者取得已配置的消息
     *
     * @param entityType 实体类型
     * @return 死亡消息
     */
    public String getByEntity(EntityType entityType) {
        if (print_unset_entity && byEntity.get(entityType.name()) == null) {
            return getSettings().getString("DeathMessages.print-unset-entity").replace("{E}", entityType.name());
        }

        return byEntity.containsKey(entityType.name()) ? getRandomFromListString(byEntity.get(entityType.name()))
                : getUnknow();
    }

    private static String getRandomFromListString(List<String> strings) {
        return strings.get(new Random().nextInt(strings.size()));
    }

    private static String getUnknow() {
        return unknow != null && unknow.size() > 0 ? unknow.get(new Random().nextInt(unknow.size())) : null;
    }
///////////////////////////////////////////////////

    private static String color(String str) {
        if (str == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
