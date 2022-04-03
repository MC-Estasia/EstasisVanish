package de.mcestasia.estasiavanish.service.entity;

import de.mcestasia.estasiavanish.EstasiaVanishBukkitPlugin;
import de.mcestasia.estasiavanish.service.DatabaseService;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public class VanishProvider {

    private final EstasiaVanishBukkitPlugin plugin;
    private final DatabaseService databaseService;
    private final String tableName;

    public VanishProvider() {
        this.plugin = EstasiaVanishBukkitPlugin.instance;
        this.databaseService = this.plugin.getDatabaseService();
        this.tableName = "vanish";
    }

    public void registerPlayer(String playerUUID) {
        if (this.isPlayerRegistered(playerUUID)) return;
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("INSERT INTO  " + this.tableName + " VALUES(?, ?, ?, ?)");
            preparedStatement.setString(1, playerUUID);
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, "");
            preparedStatement.setInt(4, 0);
            preparedStatement.execute();
            preparedStatement.close();
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Update konnte augeführt werden!");
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Update konnte nicht augeführt werden!");
            exception.printStackTrace();
        }
    }

    public void setVanished(String playerUUID, boolean inVanish) {
        if (!(this.isPlayerRegistered(playerUUID))) return;
        try {
            int vanishStatus = inVanish ? 1 : 0;
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("UPDATE `" + tableName + "` SET `VANISHED` = ? WHERE `UUID` = ?");
            preparedStatement.setInt(1, vanishStatus);
            preparedStatement.setString(2, playerUUID);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Update konnte nicht augeführt werden!");
            exception.printStackTrace();
        }
    }

    public void saveInventory(String playerUUID, String base64Inventory) {
        if (!(this.isPlayerRegistered(playerUUID))) return;
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("UPDATE `" + tableName + "` SET `INVENTORY` = ? WHERE `UUID` = ?");
            preparedStatement.setString(1, base64Inventory);
            preparedStatement.setString(2, playerUUID);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Update konnte nicht augeführt werden!");
            exception.printStackTrace();
        }
    }

    public void saveArmor(String playerUUID, String base64Armor) {
        if (!(this.isPlayerRegistered(playerUUID))) return;
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("UPDATE `" + tableName + "` SET `ARMOR` = ? WHERE `UUID` = ?");
            preparedStatement.setString(1, base64Armor);
            preparedStatement.setString(2, playerUUID);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Update konnte nicht augeführt werden!");
            exception.printStackTrace();
        }
    }

    public String getSavedInventory(String playerUUID) {
        if (!(this.isPlayerRegistered(playerUUID)) || !(this.hasInventorySaved(playerUUID))) return "";
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `UUID` = ?");
            preparedStatement.setString(1, playerUUID);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("INVENTORY");
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Query konnte nicht ausgeführt werden!");
            exception.printStackTrace();
        }
        return "";
    }

    public String getSavedArmor(String playerUUID) {
        if (!(this.isPlayerRegistered(playerUUID)) || !(this.hasArmorSaved(playerUUID))) return "";
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `UUID` = ?");
            preparedStatement.setString(1, playerUUID);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("ARMOR");
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Query konnte nicht ausgeführt werden!");
            exception.printStackTrace();
        }
        return "";
    }

    public boolean wasInVanish(String playerUUID) {
        if (!(this.isPlayerRegistered(playerUUID))) return false;
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `UUID` = ?");
            preparedStatement.setString(1, playerUUID);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return resultSet.getInt("VANISHED") == 1;
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Query konnte nicht ausgeführt werden!");
            exception.printStackTrace();
        }
        return false;
    }

    public boolean hasInventorySaved(String playerUUID) {
        if (!(this.isPlayerRegistered(playerUUID))) return false;
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `UUID` = ?");
            preparedStatement.setString(1, playerUUID);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return resultSet.getString("INVENTORY").length() > 0;
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Query konnte nicht ausgeführt werden!");
            exception.printStackTrace();
        }
        return false;
    }

    public boolean hasArmorSaved(String playerUUID) {
        if (!(this.isPlayerRegistered(playerUUID))) return false;
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `UUID` = ?");
            preparedStatement.setString(1, playerUUID);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return resultSet.getString("ARMOR").length() > 0;
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Query konnte nicht ausgeführt werden!");
            exception.printStackTrace();
        }
        return false;
    }

    public boolean isPlayerRegistered(String playerUUID) {
        try {
            final PreparedStatement preparedStatement = this.databaseService.getConnection().prepareStatement("SELECT * FROM " + this.tableName + " WHERE `UUID` = ?");
            preparedStatement.setString(1, playerUUID);
            final ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            this.plugin.getLogger().log(Level.WARNING, "[MySQL] Query konnte nicht augeführt werden!");
            exception.printStackTrace();
        }
        return false;
    }

}
