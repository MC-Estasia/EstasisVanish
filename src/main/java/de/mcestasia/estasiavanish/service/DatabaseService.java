package de.mcestasia.estasiavanish.service;

import de.mcestasia.estasiavanish.EstasiaVanishBukkitPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class DatabaseService {

    private final EstasiaVanishBukkitPlugin plugin;
    private Connection connection;

    public DatabaseService() {
        this.plugin = EstasiaVanishBukkitPlugin.instance;
        this.connect();
        this.createTables();
    }

    private void connect() {

        if (!(this.plugin.getDatabaseConfigurationManager().hasData())) {
            System.out.println("Die Verbindung zur Datenbank konnte nicht hergestellt werden, da sie noch nicht konfiguriert ist!");
            return;
        }

        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" +
                            this.plugin.getDatabaseConfigurationManager().getHost() +
                            ":" +
                            this.plugin.getDatabaseConfigurationManager().getPort() +
                            "/" +
                            this.plugin.getDatabaseConfigurationManager().getDatabase() +
                            "?autoReconnect=true",
                    this.plugin.getDatabaseConfigurationManager().getUser(),
                    this.plugin.getDatabaseConfigurationManager().getPassword());
            this.plugin.getLogger().log(Level.INFO, "[MySQL] Die Datenbankverbindung konnte hergestellt werden!");
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Die Datenbankverbindung konnte nicht hergestellt werden!");
        }
    }

    public void disconnect() {
        try {
            this.connection.close();
            this.plugin.getLogger().log(Level.INFO, "[MySQL] Die Datenbankverbindung konnte geschlossen werden!");
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Die Datenbankverbindung konnte nicht geschlossen werden!");
        }
    }

    public void update(final String query) {
        try {
            final Statement statement = this.connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException exception) {
            this.plugin.getLogger().info("[MySQL] Das Update konnte nicht ausgeführt werden!");
            exception.printStackTrace();
        }
    }

    private void createTables() {
        this.update("CREATE TABLE IF NOT EXISTS vanish(UUID VARCHAR(255), INVENTORY VARCHAR(1024), ARMOR VARCHAR(1024), VANISHED INT(1))");
    }

}
