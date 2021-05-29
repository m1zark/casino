/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.casino.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class SQLStatements {
    private String mainTable;

    public SQLStatements(String mainTable) {
        this.mainTable = mainTable;
    }

    public void createTables() {
        try (Connection connection = DataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + this.mainTable + "` (ID INTEGER NOT NULL AUTO_INCREMENT, PlayerUUID CHAR(36), Balance Integer, PRIMARY KEY(ID));")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerData(UUID uuid) {
        try (Connection connection = DataSource.getConnection()) {
            try (ResultSet results = connection.prepareStatement("SELECT * FROM `" + this.mainTable + "` WHERE playerUUID = '" + uuid + "'").executeQuery()) {
                if (!results.next()) {
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + this.mainTable + "` (PlayerUUID, Balance) VALUES (?, ?)")) {
                        statement.setString(1, uuid.toString());
                        statement.setInt(2, 0);
                        statement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBalanceTotal(UUID uuid, boolean purchase, int amount) {
        this.addPlayerData(uuid);
        try (Connection connection = DataSource.getConnection()) {
            if (purchase) {
                try(PreparedStatement updatePlayer = connection.prepareStatement("UPDATE `" + this.mainTable + "` SET Balance = Balance - " + amount + " WHERE PlayerUUID = '" + uuid + "'")){
                    updatePlayer.executeUpdate();
                }
            } else {
                try(PreparedStatement updatePlayer = connection.prepareStatement("UPDATE `" + this.mainTable + "` SET Balance = Balance + " + amount + " WHERE PlayerUUID = '" + uuid + "'")){
                    updatePlayer.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBalance(UUID uuid) {
        try (Connection connection = DataSource.getConnection()) {
            try (ResultSet results = connection.prepareStatement("SELECT * FROM `" + this.mainTable + "` WHERE playerUUID = '" + uuid + "'").executeQuery()) {
                if(results.next()) {
                    return results.getInt("Balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        
        return 0;
    }
}

