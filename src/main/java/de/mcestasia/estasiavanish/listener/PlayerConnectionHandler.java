package de.mcestasia.estasiavanish.listener;

import de.mcestasia.estasiavanish.EstasiaVanishBukkitPlugin;
import de.mcestasia.estasiavanish.model.VanishPlayer;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

import static de.mcestasia.estasiavanish.util.Constants.*;

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
            this.plugin.getVanishProvider().registerPlayer(player.getUniqueId().toString());

            if (this.plugin.getVanishProvider().wasInVanish(player.getUniqueId().toString())) {
                vanishList.add(player);
                this.plugin.getVanishManager().setVanishWithoutInventory(player);
                player.setGameMode(GameMode.CREATIVE);
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> player.setGameMode(GameMode.CREATIVE), 5 * 20L);

                vanishPlayers.add(this.plugin.getVanishManager().fetchVanishPlayer(player));
            }
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
        final Player player = event.getPlayer();
        if (vanishList.contains(player)) {
            vanishList.remove(player);
            Optional<VanishPlayer> optionalVanishPlayer = vanishPlayers.stream().filter(vanishPlayer -> vanishPlayer.getUuid().equals(player.getUniqueId())).findFirst();

            player.getActivePotionEffects().clear();
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);

            optionalVanishPlayer.ifPresent(vanishPlayers::remove);

            this.plugin.getVanishManager().stopHotbarTask(player);
            player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(5, 100), false);
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                onlinePlayer.showPlayer(EstasiaVanishBukkitPlugin.getPlugin(EstasiaVanishBukkitPlugin.class), player);
            }
        }
    }

}
