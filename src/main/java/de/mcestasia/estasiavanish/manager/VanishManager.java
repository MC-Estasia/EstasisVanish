package de.mcestasia.estasiavanish.manager;

import de.mcestasia.estasiavanish.EstasiaVanishBukkitPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

import static de.mcestasia.estasiavanish.util.Constants.*;

public class VanishManager {

    private final EstasiaVanishBukkitPlugin plugin;
    private final VanishInventoryManager vanishInventoryManager;

    public VanishManager() {
        this.plugin = EstasiaVanishBukkitPlugin.instance;
        this.vanishInventoryManager = this.plugin.getVanishInventoryManager();
    }

    public void setPlayerVanish(Player toVanish) {
        toVanish.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Du bist nun im §c§lVanish§7!"));
        vanishList.add(toVanish);
        vanishArmorInventories.put(toVanish, this.vanishInventoryManager.playerArmorToBase64(toVanish.getInventory()));
        vanishItemInventories.put(toVanish, this.vanishInventoryManager.playerInventoryToBase64(toVanish.getInventory()));
        toVanish.sendMessage(PREFIX + "Du bist nun §c§lunsichtbar§7!");
        toVanish.setGameMode(GameMode.CREATIVE);

        this.plugin.getServer().getOnlinePlayers().stream().filter(player -> !(player.hasPermission("de.mcestasia.estasiavanish.bypass"))).forEach(player -> {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            EntityPlayer entityPlayer = ((CraftPlayer) toVanish).getHandle();
            player.hidePlayer(this.plugin, toVanish);
            connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        });

        toVanish.getInventory().clear();

        this.plugin.getServer().getOnlinePlayers().stream().filter(player -> player.hasPermission("de.mcestasia.estasiavanish.notification") && !(player == toVanish)).forEach(player -> player.sendMessage(PREFIX + "Der Spieler §e§l" + toVanish.getName() + " §7ist nun im §4§lVanish§7."));
        this.startHotbarTask(toVanish);
        toVanish.playSound(toVanish.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, .7F, .7F);
    }

    public void removePlayerVanish(Player fromVanish) {
        vanishList.remove(fromVanish);
        fromVanish.setGameMode(GameMode.SURVIVAL);
        fromVanish.sendMessage(PREFIX + "Du bist nun wieder §a§lsichtbar§7!");
        fromVanish.getInventory().clear();
        try {
            for (ItemStack itemStack : this.vanishInventoryManager.itemStackArrayFromBase64(vanishItemInventories.get(fromVanish))) {
                if (itemStack == null) continue;
                fromVanish.getInventory().addItem(itemStack);
            }
            fromVanish.getInventory().setArmorContents(this.vanishInventoryManager.itemStackArrayFromBase64(vanishArmorInventories.get(fromVanish)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fromVanish.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Du bist nun wieder §a§lsichtbar§7!"));
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            onlinePlayer.showPlayer(this.plugin, fromVanish);
        }
        this.plugin.getServer().getOnlinePlayers().stream().filter(player -> player.hasPermission("de.mcestasia.estasiavanish.notification") && !(player == fromVanish)).forEach(player -> player.sendMessage(PREFIX + "Der Spieler §e§l" + fromVanish.getName() + " §7ist nun wieder §a§lsichtbar§7."));
        fromVanish.playSound(fromVanish.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.3F, 1.3F);
        this.stopHotbarTask(fromVanish);
        vanishItemInventories.remove(fromVanish);
        vanishArmorInventories.remove(fromVanish);
    }

    private void startHotbarTask(Player toVanish) {
        vanishTaskMap.put(toVanish, this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            toVanish.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Du bist im §4§lVanish§7!"));
        }, 0, 2 * 20L));
    }

    private void stopHotbarTask(Player toVanish) {
        vanishTaskMap.get(toVanish).cancel();
        vanishTaskMap.remove(toVanish);
    }

}
