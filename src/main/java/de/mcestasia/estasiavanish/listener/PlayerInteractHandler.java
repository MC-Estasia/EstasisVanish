package de.mcestasia.estasiavanish.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

import static de.mcestasia.estasiavanish.util.Constants.vanishList;

public class PlayerInteractHandler implements Listener {

    @EventHandler
    public void handlePlayerInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        final Player vanishPlayer = event.getPlayer();
        if (!(vanishList.contains(vanishPlayer))) return;
        if (vanishPlayer.isSneaking()) {
            final Inventory targetInventory = ((Player) event.getRightClicked()).getInventory();
            vanishPlayer.openInventory(targetInventory);
            return;
        }

        final Player target = (Player) event.getRightClicked();
        target.addPassenger(vanishPlayer);
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) return;
        if (!(vanishList.contains(player))) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleChestClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !(vanishList.contains(event.getPlayer())))
            return;
        final Block block = event.getClickedBlock();
        final BlockState state = block.getState();
        if (!(state instanceof Chest)) return;
        event.setCancelled(true);
        final Player player = event.getPlayer();
        Chest chest = (Chest) Objects.requireNonNull(event.getClickedBlock()).getState();
        Inventory chestInventory = Bukkit.createInventory(player, chest.getInventory().getSize(), "§c§lTruhen-Inventar");
        chestInventory.setContents(chest.getInventory().getContents());
        player.openInventory(chestInventory);
    }

}
