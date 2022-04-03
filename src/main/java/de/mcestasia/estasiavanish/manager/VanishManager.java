package de.mcestasia.estasiavanish.manager;

import de.mcestasia.estasiavanish.EstasiaVanishBukkitPlugin;
import de.mcestasia.estasiavanish.model.VanishPlayer;
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
import org.bukkit.potion.PotionEffectType;
import se.file14.procosmetics.api.ProCosmeticsAPI;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static de.mcestasia.estasiavanish.util.Constants.*;

public class VanishManager {

    private final EstasiaVanishBukkitPlugin plugin;
    private final VanishInventoryManager vanishInventoryManager;

    public VanishManager() {
        this.plugin = EstasiaVanishBukkitPlugin.instance;
        this.vanishInventoryManager = this.plugin.getVanishInventoryManager();
    }

    public void setPlayerVanish(Player toVanish) {

        String armorBase64 = this.vanishInventoryManager.playerArmorToBase64(toVanish.getInventory());
        String inventoryBase64 = this.vanishInventoryManager.playerInventoryToBase64(toVanish.getInventory());

        this.createVanishPlayer(toVanish,
                inventoryBase64,
                armorBase64
        );

        toVanish.sendMessage(PREFIX + "Du bist nun §c§lunsichtbar§7!");
        toVanish.setGameMode(GameMode.CREATIVE);
        toVanish.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(Integer.MAX_VALUE, 100), false);
        toVanish.playSound(toVanish.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, .7F, .7F);
        this.startHotbarTask(toVanish);

        vanishList.add(toVanish);

        hidePlayer(toVanish);

        this.plugin.getServer().getOnlinePlayers().stream().filter(player -> player.hasPermission("de.mcestasia.estasiavanish.notification") && !(player == toVanish)).forEach(player -> player.sendMessage(PREFIX + "Der Spieler §e§l" + toVanish.getName() + " §7ist nun im §4§lVanish§7."));

        // Database
        this.plugin.getVanishProvider().setVanished(toVanish.getUniqueId().toString(), true);
        this.plugin.getVanishProvider().saveInventory(toVanish.getUniqueId().toString(), inventoryBase64);
        this.plugin.getVanishProvider().saveArmor(toVanish.getUniqueId().toString(), armorBase64);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            toVanish.getInventory().clear();
        }, 10L);

        Objects.requireNonNull(ProCosmeticsAPI.getUser(toVanish.getPlayer())).fullyUnequipCosmetics(true);
    }

    private void hidePlayer(Player toVanish) {
        this.plugin.getServer().getOnlinePlayers().stream().filter(player -> !(player.hasPermission("de.mcestasia.estasiavanish.bypass"))).forEach(player -> {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            EntityPlayer entityPlayer = ((CraftPlayer) toVanish).getHandle();
            player.hidePlayer(this.plugin, toVanish);
            connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        });
    }

    public void removePlayerVanish(Player fromVanish) throws IOException {
        vanishList.remove(fromVanish);

        fromVanish.setGameMode(GameMode.SURVIVAL);
        fromVanish.sendMessage(PREFIX + "Du bist nun wieder §a§lsichtbar§7!");

        Optional<VanishPlayer> optionalVanishPlayer = vanishPlayers.stream().filter(vanishPlayer -> vanishPlayer.getUuid().equals(fromVanish.getUniqueId())).findFirst();
        if (optionalVanishPlayer.isEmpty()) return;
        fromVanish.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Du bist nun wieder §a§lsichtbar§7!"));
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            onlinePlayer.showPlayer(this.plugin, fromVanish);
        }
        this.plugin.getServer().getOnlinePlayers().stream().filter(player -> player.hasPermission("de.mcestasia.estasiavanish.notification") && !(player == fromVanish)).forEach(player -> player.sendMessage(PREFIX + "Der Spieler §e§l" + fromVanish.getName() + " §7ist nun wieder §a§lsichtbar§7."));
        fromVanish.playSound(fromVanish.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.3F, 1.3F);
        this.stopHotbarTask(fromVanish);

        ItemStack[] inventoryStack = this.vanishInventoryManager.itemStackArrayFromBase64(optionalVanishPlayer.get().getInventory());
        ItemStack[] armorStack = this.vanishInventoryManager.itemStackArrayFromBase64(optionalVanishPlayer.get().getArmor());

        fromVanish.getInventory().setContents(inventoryStack);
        fromVanish.getInventory().setArmorContents(armorStack);

        fromVanish.getActivePotionEffects().clear();
        fromVanish.removePotionEffect(PotionEffectType.NIGHT_VISION);
        // Database
        this.plugin.getVanishProvider().setVanished(fromVanish.getUniqueId().toString(), false);
        this.plugin.getVanishProvider().saveInventory(fromVanish.getUniqueId().toString(), "");
        this.plugin.getVanishProvider().saveArmor(fromVanish.getUniqueId().toString(), "");

        vanishPlayers.remove(optionalVanishPlayer.get());
    }

    public void setVanishWithoutInventory(Player toVanish) {
        toVanish.sendMessage(PREFIX + "Du bist immernoch §c§lunsichtbar§7!");

        hidePlayer(toVanish);

        toVanish.getInventory().clear();

        toVanish.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(Integer.MAX_VALUE, 100), false);
        this.plugin.getServer().getOnlinePlayers().stream().filter(player -> player.hasPermission("de.mcestasia.estasiavanish.notification") && !(player == toVanish)).forEach(player -> player.sendMessage(PREFIX + "Der Spieler §e§l" + toVanish.getName() + " §7ist im §4§lVanish§7."));
        this.startHotbarTask(toVanish);
        toVanish.playSound(toVanish.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, .7F, .7F);
        toVanish.setGameMode(GameMode.CREATIVE);
    }

    private void startHotbarTask(Player toVanish) {
        vanishTaskMap.put(toVanish, this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            toVanish.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Du bist im §4§lVanish§7!"));
        }, 0, 2 * 20L));
    }

    public void stopHotbarTask(Player toVanish) {
        vanishTaskMap.get(toVanish).cancel();
        vanishTaskMap.remove(toVanish);
    }

    public void createVanishPlayer(Player player, String inventory, String armor) {
        vanishPlayers.add(new VanishPlayer(player.getUniqueId(), inventory, armor));
    }

    public VanishPlayer fetchVanishPlayer(Player player) {
        if (!(this.plugin.getVanishProvider().hasInventorySaved(player.getUniqueId().toString()))) return null;
        return vanishPlayers.stream().filter(vanishPlayer -> vanishPlayer.getUuid().equals(player.getUniqueId())).findFirst().orElse(new VanishPlayer(player.getUniqueId(), this.plugin.getVanishProvider().getSavedInventory(player.getUniqueId().toString()), this.plugin.getVanishProvider().getSavedArmor(player.getUniqueId().toString())));
    }

}
