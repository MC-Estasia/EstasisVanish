package de.mcestasia.estasiavanish.util;

import de.mcestasia.estasiavanish.model.VanishPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constants {

    public static final String PREFIX = "§8[§2Estasia§aVanish§8] §7";
    public static final String NO_PERMISSION_MESSAGE = PREFIX + "Dafür hast du §ckeine §7Rechte!";

    public static final List<Player> vanishList = new ArrayList<>();
    public static final List<VanishPlayer> vanishPlayers = new ArrayList<>();
    public static final HashMap<Player, BukkitTask> vanishTaskMap = new HashMap<>();

}
