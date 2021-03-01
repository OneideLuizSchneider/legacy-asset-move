package com.legacy.asset.move;

import com.legacy.asset.move.config.ConnectionS3;
import com.legacy.asset.move.config.DataSource;
import com.legacy.asset.move.dto.Image;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Migration {

    private static final Dotenv dotenv = Dotenv.load();

    public void execute() {
        while (exists()) {
            try {
                this.start();
            } catch (SQLException throwables) {
                System.err.println("Error on method start------------------");
                System.err.println(throwables.getMessage());
                System.err.println("---------------------------------------");
            }
        }
    }

    private boolean exists() {
        String sqlCount = "select count(id) as total from `production-db`.images where path not like 'avatar_%' and id % ? = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCount)) {
            stmt.setInt(1, Integer.parseInt(dotenv.get("BOT_TOTAL")));
            stmt.setInt(2, Integer.parseInt(dotenv.get("BOT_NUMER")));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("total") > 0;
        } catch (SQLException throwables) {
            System.err.println("Error on method exists-----------------------");
            System.err.println(throwables.getMessage());
            System.err.println("---------------------------------------------");
        }
        return false;
    }

    private void start() throws SQLException {
        String sqlImages = "select id, path from `production-db`.images where path not like 'avatar_%' and id % ? = ? limit ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlImages)) {
            stmt.setInt(1, Integer.parseInt(dotenv.get("BOT_TOTAL")));
            stmt.setInt(2, Integer.parseInt(dotenv.get("BOT_NUMER")));
            stmt.setInt(3, Integer.parseInt(dotenv.get("THREADS")));
            ResultSet rs = stmt.executeQuery();

            ThreadPoolExecutor executor = getThreadPool();

            while (rs.next()) {
                Image dto = new Image(
                        rs.getInt("id"),
                        rs.getString("path"),
                        rs.getString("path").replace(
                                dotenv.get("OLD_PATH"),
                                dotenv.get("NEW_PATH")
                        )
                );
                executor.submit(() -> {
                    new ConnectionS3().copyObject(
                            dto.getOldPath(),
                            dto.getNewPath()
                    );
                    new DbUtil().execute(dto);
                });
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    private ThreadPoolExecutor getThreadPool() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.parseInt(dotenv.get("THREADS")));
    }

}
