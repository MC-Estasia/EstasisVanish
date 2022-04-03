package de.mcestasia.estasiavanish.command;

import de.mcestasia.estasiavanish.EstasiaVanishBukkitPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

import static de.mcestasia.estasiavanish.util.Constants.*;

public class VanishCommand implements CommandExecutor {

    private final EstasiaVanishBukkitPlugin plugin;

    public VanishCommand() {
        this.plugin = EstasiaVanishBukkitPlugin.instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof final Player player)) {
            commandSender.sendMessage("Diesen Befehl kann nur ein Spieler ausführen!");
            return false;
        }

        if (!(player.hasPermission("de.mcestasia.estasiavanish.command.vanish"))) {
            player.sendMessage(NO_PERMISSION_MESSAGE);
            return false;
        }

        if (!(vanishList.contains(player))) {
            this.plugin.getVanishManager().setPlayerVanish(player);
            return true;
        }

        try {
            this.plugin.getVanishManager().removePlayerVanish(player);
        } catch (IOException exception) {
            player.sendMessage(PREFIX + "Ein §cFehler ist §7aufgetreten!");
            exception.printStackTrace();
        }

        return true;
    }


}
