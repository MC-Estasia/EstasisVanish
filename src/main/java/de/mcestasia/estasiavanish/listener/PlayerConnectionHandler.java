package de.mcestasia.estasiavanish.listener;

import de.mcestasia.estasiavanish.EstasiaVanishBukkitPlugin;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import static de.mcestasia.estasiavanish.util.Constants.PREFIX;
import static de.mcestasia.estasiavanish.util.Constants.vanishList;

public class PlayerConnectionHandler implements Listener {

    private EstasiaVanishBukkitPlugin plugin;

    public PlayerConnectionHandler() {
        this.plugin = EstasiaVanishBukkitPlugin.instance;
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("de.mcestasia.estasiavanish.bypass")) {
            vanishList.forEach(vanishPlayer -> player.sendMessage(PREFIX + "Der Spieler §c§l" + vanishPlayer.getName() + " §7ist im §4§lVanish§7!"));
            return;
        }

        vanishList.forEach(vanishPlayer -> {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            EntityPlayer entityPlayer = ((CraftPlayer) vanishPlayer).getHandle();
            player.hidePlayer(this.plugin, vanishPlayer);
            connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        });
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        if (vanishList.contains(event.getPlayer())) {
            vanishList.remove(event.getPlayer());
            final Player player = event.getPlayer();
            player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(5, 100), false);
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                onlinePlayer.showPlayer(EstasiaVanishBukkitPlugin.getPlugin(EstasiaVanishBukkitPlugin.class), player);
            }
        }
    }

}
