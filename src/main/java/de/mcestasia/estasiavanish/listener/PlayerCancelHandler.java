package de.mcestasia.estasiavanish.listener;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

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

}
