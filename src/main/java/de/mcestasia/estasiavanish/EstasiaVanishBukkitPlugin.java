package de.mcestasia.estasiavanish;

import de.mcestasia.estasiavanish.command.VanishCommand;
import de.mcestasia.estasiavanish.command.VanishReloadCommand;
import de.mcestasia.estasiavanish.listener.PlayerCancelHandler;
import de.mcestasia.estasiavanish.listener.PlayerConnectionHandler;
import de.mcestasia.estasiavanish.listener.PlayerInteractHandler;
import de.mcestasia.estasiavanish.manager.DatabaseConfigurationManager;
import de.mcestasia.estasiavanish.manager.VanishInventoryManager;
import de.mcestasia.estasiavanish.manager.VanishManager;
import de.mcestasia.estasiavanish.service.DatabaseService;
import de.mcestasia.estasiavanish.service.entity.VanishProvider;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class EstasiaVanishBukkitPlugin extends JavaPlugin {

    public static EstasiaVanishBukkitPlugin instance;

    private VanishManager vanishManager;
    private VanishInventoryManager vanishInventoryManager;
    private DatabaseConfigurationManager databaseConfigurationManager;
    private DatabaseService databaseService;
    private VanishProvider vanishProvider;

    @Override
    public void onEnable() {
        this.init();
    }

    @Override
    public void onDisable() {
        instance = null;
        this.databaseService.disconnect();
    }

    public void reload() {
        this.databaseConfigurationManager.reload();
    }

    private void init() {
        instance = this;
        this.vanishInventoryManager = new VanishInventoryManager();
        this.vanishManager = new VanishManager();
        this.databaseConfigurationManager = new DatabaseConfigurationManager();
        this.databaseService = new DatabaseService();
        this.vanishProvider = new VanishProvider();

        this.getCommand("vanish").setExecutor(new VanishCommand());
        this.getCommand("vanishreload").setExecutor(new VanishReloadCommand());

        this.getServer().getPluginManager().registerEvents(new PlayerConnectionHandler(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteractHandler(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerCancelHandler(), this);
    }

}
