/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.zaxxer.hikari.HikariConfig
 *  com.zaxxer.hikari.HikariDataSource
 */
package com.m1zark.casino.storage;

import com.m1zark.casino.Casino;
import com.m1zark.casino.storage.SQLStatements;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DataSource
extends SQLStatements {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public DataSource(String mainTable) {
        super(mainTable);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void shutdown() {
        if (ds != null) {
            ds.close();
        }
    }

    static {
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("URL", "jdbc:h2:" + Casino.getInstance().getConfigDir() + "/data/player-storage;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MSSQLServer");
        config.setPoolName("Casino");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000L);
        config.setConnectionTimeout(5000L);
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(10L));
        config.setInitializationFailTimeout(1L);
        config.setConnectionTestQuery("/* Casino ping */ SELECT 1");
        ds = new HikariDataSource(config);
    }
}

