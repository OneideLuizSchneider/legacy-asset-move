package com.legacy.asset.move.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private static final Dotenv dotenv = Dotenv.load();

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setJdbcUrl(dotenv.get("DB_URI"));
        config.setDriverClassName("org.mariadb.jdbc.MariaDbDataSource");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        ds = new HikariDataSource(config);
    }

    private DataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void close() {
        ds.close();
    }
}
