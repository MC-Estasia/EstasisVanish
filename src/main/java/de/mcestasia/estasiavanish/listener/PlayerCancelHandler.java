package de.mcestasia.estasiavanish.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static de.mcestasia.estasiavanish.util.Constants.vanishList;

public class PlayerCancelHandler implements Listener {

    @EventHandler
    public void handlePickup(final EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player) || !(vanishList.contains((Player) event.getEntity()))) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handlePlayerDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(vanishList.contains((Player) event.getDamager()))) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleTarget(final EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Zombie) || !(event.getTarget() instanceof Player) || !(vanishList.contains((Player) event.getTarget())))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        if (!(vanishList.contains(event.getPlayer()))) return;
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            assert block != null;
            if (block.getType() == Material.FARMLAND) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }

}
