package de.mcestasia.estasiavanish.manager;

import de.mcestasia.estasiavanish.EstasiaVanishBukkitPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class DatabaseConfigurationManager {

    private final EstasiaVanishBukkitPlugin plugin;
    private final File configurationFile;
    private FileConfiguration configuration;

    public DatabaseConfigurationManager() {
        this.plugin = EstasiaVanishBukkitPlugin.instance;
        this.configurationFile = new File(this.plugin.getDataFolder(), "database.yml");
        this.createDatabaseFile();
        this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);

        this.createDefaultData();
    }

    private void createDatabaseFile() {

        if (!(this.configurationFile.exists())) {
            try {
                if (this.configurationFile.createNewFile()) {
                    System.out.println("[!] Datenbank Datei erfolgreich erstellt!");
                }
            } catch (IOException exception) {
                System.out.println("[»] Datenbank Datei konnte nicht angelegt werden.");
                exception.printStackTrace();
            }
        } else {
            System.out.println("[»] Die Datenbank Datei wurde gefunden.");
        }

    }

    public void createDefaultData() {
        if (this.hasData()) {
            System.out.println("[»] Die Datenbank wurde bereits eingerichtet.");
            return;
        }

        configuration.set("database.setup", "false");
        configuration.set("database.host", "localhost");
        configuration.set("database.port", 3306);
        configuration.set("database.database", "vanish");
        configuration.set("database.user", "user");
        configuration.set("database.password", "password");

        try {
            this.configuration.save(this.configurationFile);
            System.out.println("[!] Die Standartwerte wurden erfolgreich gespeichert!");
        } catch (IOException exception) {
            System.out.println("[!] Die Standartwerte konnten nicht gespeichert werden!");
            exception.printStackTrace();
        }

    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
    }

    public boolean hasData() {
        return this.configuration.getBoolean("database.setup");
    }

    public String getHost() {
        return this.configuration.getString("database.host");
    }

    public int getPort() {
        return this.configuration.getInt("database.port");
    }

    public String getDatabase() {
        return this.configuration.getString("database.database");
    }

    public String getUser() {
        return this.configuration.getString("database.user");
    }

    public String getPassword() {
        return this.configuration.getString("database.password");
    }
}
